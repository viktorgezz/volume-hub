package ru.viktorgezz.api_T_connector.candle.service.impl;

import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;
import ru.tinkoff.piapi.core.stream.MarketDataSubscriptionService;
import ru.tinkoff.piapi.core.utils.MapperUtils;
import ru.viktorgezz.api_T_connector.candle.CandleMessage;
import ru.viktorgezz.api_T_connector.candle.service.interf.CandleStreamService;
import ru.viktorgezz.api_T_connector.company.ClientFigi;
import ru.viktorgezz.api_T_connector.rabbitmq.RabbitMQProducer;
import ru.viktorgezz.api_T_connector.ConnectTApiInvest;

import java.util.function.Consumer;

import static ru.tinkoff.piapi.core.utils.DateUtils.timestampToString;

@Component
public class CandleStreamServiceImpl implements CandleStreamService {

    private static final Logger log = LoggerFactory.getLogger(CandleStreamServiceImpl.class);

    private static final String message_subscription_terminated = "Подписка разорвана";

    private final MarketDataStreamService marketDataStreamService;
    private final RabbitMQProducer producer;
    private final ClientFigi clientFigi;

    private MarketDataSubscriptionService streamSubscription;

    @Autowired
    public CandleStreamServiceImpl(
            ConnectTApiInvest apiInvest,
            RabbitMQProducer producer,
            ClientFigi clientFigi
    ) {
        this.marketDataStreamService = apiInvest.getInvestApi().getMarketDataStreamService();
        this.producer = producer;
        this.clientFigi = clientFigi;
    }

    public void streamLatestMinuteCandles() {
        Consumer<Throwable> onErrorCallback = error -> log.error("Ошибка stream: {}", error.getMessage());

        try {
            this.streamSubscription = this.marketDataStreamService
                    .newStream(
                            "candles_stream",
                            event -> {
                                if (event.hasCandle()) {
                                    Candle candle = event.getCandle();
                                    log.debug("Latest minute candle for FIGI {}: Open={}, High={}, Low={}, Close={}, Volume={}, Time={}",
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
                                    log.debug("Candle send. Figi: {}", candle.getFigi());
                                }

                            },
                            onErrorCallback);

            this.streamSubscription.subscribeCandles(
                    clientFigi.fetchAllFigis(),
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
