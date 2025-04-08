package ru.viktorgezz.api_T_connector.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;
import ru.tinkoff.piapi.core.utils.MapperUtils;
import ru.viktorgezz.api_T_connector.service.interf.CandleStreamService;
import ru.viktorgezz.api_T_connector.util.ConnectTApiInvest;

import java.util.List;
import java.util.function.Consumer;

import static ru.tinkoff.piapi.core.utils.DateUtils.timestampToString;

/// invest-api-java-sdk/example/basic-example/src/main/java/ru/tinkoff/piapi/example
@Component
public class CandleStreamServiceImpl implements CandleStreamService {

    private static final Logger log = LoggerFactory.getLogger(CandleStreamServiceImpl.class);

    private final MarketDataStreamService marketDataStreamService;

    @Autowired
    public CandleStreamServiceImpl(ConnectTApiInvest apiInvest) {
        this.marketDataStreamService = apiInvest.getInvestApi().getMarketDataStreamService();
    }

    public void streamLatestMinuteCandles(List<String> figis) {
        Consumer<Throwable> onErrorCallback = error -> log.error("Stream error: ", error);
        
        var stream = this.marketDataStreamService
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
                                        timestampToString(candle.getTime()));
                            }
                        },
                        onErrorCallback);

        stream.subscribeCandles(
                figis,
                SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE,
                true);

    }
}
