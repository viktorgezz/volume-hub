package ru.viktorgezz.api_T_connector.service.impl;

import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;
import ru.tinkoff.piapi.core.stream.MarketDataSubscriptionService;
import ru.tinkoff.piapi.core.utils.MapperUtils;
import ru.viktorgezz.api_T_connector.model.CandleMessage;
import ru.viktorgezz.api_T_connector.service.interf.CandleStreamService;
import ru.viktorgezz.api_T_connector.service.interf.ShareService;
import ru.viktorgezz.api_T_connector.util.ConnectTApiInvest;

import java.util.function.Consumer;

import static ru.tinkoff.piapi.core.utils.DateUtils.timestampToString;

@Component
public class CandleStreamServiceImpl implements CandleStreamService {

    private static final Logger log = LoggerFactory.getLogger(CandleStreamServiceImpl.class);

    private static final String message_subscription_terminated = "Подписка разорвана";

    private final MarketDataStreamService marketDataStreamService;
    private final RabbitMQProducerImpl producer;
    private final ShareService shareService;

    private MarketDataSubscriptionService streamSubscription;

    @Autowired
    public CandleStreamServiceImpl(
            ConnectTApiInvest apiInvest,
            RabbitMQProducerImpl producer,
            ShareService shareService
    ) {
        this.marketDataStreamService = apiInvest.getInvestApi().getMarketDataStreamService();
        this.producer = producer;
        this.shareService = shareService;
    }

    public void streamLatestMinuteCandles() {
        Consumer<Throwable> onErrorCallback = error -> log.error("Stream error: ", error);

        try {
            this.streamSubscription = this.marketDataStreamService
                    .newStream(
                            "candles_stream",
                            event -> {
                                if (event.hasCandle()) {
                                    Candle candle = event.getCandle();
                                    log.info("Latest minute candle for FIGI {}: Open={}, High={}, Low={}, Close={}, Volume={}, Time={}",
                                            candle.getFigi(),
                                            MapperUtils.quotationToBigDecimal(candle.getOpen()),
                                            MapperUtils.quotationToBigDecimal(candle.getHigh()),
                                            MapperUtils.quotationToBigDecimal(candle.getLow()),
                                            MapperUtils.quotationToBigDecimal(candle.getClose()),
                                            candle.getVolume(),
                                            timestampToString(candle.getTime())
                                    );

                                    CandleMessage candleMessage = new CandleMessage(
                                            candle.getFigi(),
                                            MapperUtils.quotationToBigDecimal(candle.getOpen()),
                                            MapperUtils.quotationToBigDecimal(candle.getClose()),
                                            MapperUtils.quotationToBigDecimal(candle.getHigh()),
                                            MapperUtils.quotationToBigDecimal(candle.getLow()),
                                            candle.getVolume(),
                                            timestampToString(candle.getTime())
                                    );

                                    producer.sendCandle(candleMessage);
                                    log.info("Candle send. Figi: {}", candle.getFigi());
                                }

                            },
                            onErrorCallback);

            this.streamSubscription.subscribeCandles(
                    shareService.getAllFigis(),
                    SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE,
                    true);
        } catch (StatusRuntimeException e) {
            log.warn(message_subscription_terminated);
        }
    }

    public void cancelStream() {
        try {
            this.streamSubscription.cancel();
            log.info("Подписка разорвана");
        } catch (StatusRuntimeException e) {
            log.warn(message_subscription_terminated);
        }
    }
}
