package ru.viktorgezz.api_T_connector.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.InstrumentsService;
import ru.tinkoff.piapi.core.MarketDataService;
import ru.viktorgezz.api_T_connector.model.CustomCandle;
import ru.viktorgezz.api_T_connector.service.interf.HistoricalCandleMarketService;
import ru.viktorgezz.api_T_connector.util.ConnectTApiInvest;
import ru.viktorgezz.api_T_connector.util.FigiList;
import ru.viktorgezz.api_T_connector.util.ShareDao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static ru.tinkoff.piapi.contract.v1.InstrumentStatus.INSTRUMENT_STATUS_BASE;
import static ru.tinkoff.piapi.core.utils.MapperUtils.mapUnitsAndNanos;

@Service
public class HistoricalCandleMarketServiceImpl implements HistoricalCandleMarketService {

    private static final Logger log = LoggerFactory.getLogger(HistoricalCandleMarketServiceImpl.class);

    private static final int ONE_DAY_IN_MINUTES = 1440;
    private static final int ONE_HOUR_IN_MINUTES = 60;
    private static final String CANDLE_FETCH_MESSAGE = "Получение свечи с figi {} за последние минут {}";

    private final InstrumentsService instrumentsService;
    private final MarketDataService marketDataService;


    private final List<String> figis;

    @Autowired
    public HistoricalCandleMarketServiceImpl(
            ConnectTApiInvest apiInvest,
            FigiList figiListObject) {
        this.instrumentsService = apiInvest.getInvestApi().getInstrumentsService();
        this.marketDataService = apiInvest.getInvestApi().getMarketDataService();
        this.figis = figiListObject.getFigis();
    }

    public Map<String, List<CustomCandle>> getMinuteCandlesForLastHourByFigi(
            final String figi) {
        log.info(CANDLE_FETCH_MESSAGE, figi, ONE_HOUR_IN_MINUTES);
        return Collections.singletonMap(figi, fetchCandlesByFigiAndTime(figi, ONE_HOUR_IN_MINUTES));
    }

    public Map<String, List<CustomCandle>> getMinuteCandlesForLastDayAllFigis() {
        log.info(CANDLE_FETCH_MESSAGE, "Всех свечей", ONE_DAY_IN_MINUTES);
        return figis
                .stream()
                .collect(Collectors.toMap(
                        figi -> figi,
                        figi -> fetchCandlesByFigiAndTime(figi, ONE_DAY_IN_MINUTES)
                ));
    }

    private List<CustomCandle> fetchCandlesByFigiAndTime(String figi, int minutesBack) {
        List<CustomCandle> candles = getMinuteCandlesForTimeByFigi(figi, minutesBack);
        log.info("Для figi {} получено {} минутных свечей", figi, candles.size());
        return candles;
    }

    private List<CustomCandle> getMinuteCandlesForTimeByFigi(
            final String figi,
            final int minutesBack
    ) {
        try {
            return marketDataService.getCandlesSync(
                            figi,
                            Instant.now().minus(minutesBack, ChronoUnit.MINUTES),
                            Instant.now(),
                            CandleInterval.CANDLE_INTERVAL_1_MIN
                    )
                    .stream().map(candle -> new CustomCandle(
                            convertQuotationToBigDecimal(candle.getOpen()),
                            convertQuotationToBigDecimal(candle.getClose()),
                            convertQuotationToBigDecimal(candle.getHigh()),
                            convertQuotationToBigDecimal(candle.getLow()),
                            candle.getVolume(),
                            convertProtobufToSqlTimestamp(candle.getTime())
                    ))
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error("Figi: {} not found. Error: {}", figi, e.getMessage());
            return new ArrayList<>();
        }
    }

    private static BigDecimal convertQuotationToBigDecimal(Quotation price) {
        if (price == null) {
            log.error("Price equal null");
            return BigDecimal.ZERO;
        }
        return mapUnitsAndNanos(price.getUnits(), price.getNano());
    }

    private static Timestamp convertProtobufToSqlTimestamp(com.google.protobuf.Timestamp time) {
        return java.sql.Timestamp.from(Instant.ofEpochSecond(time.getSeconds(), time.getNanos()));
    }

    private void printAllFigis() {
        System.out.println(this.instrumentsService.getSharesSync(INSTRUMENT_STATUS_BASE));
    }
}
