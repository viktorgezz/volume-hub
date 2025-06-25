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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static ru.tinkoff.piapi.contract.v1.CandleInterval.*;
import static ru.tinkoff.piapi.contract.v1.InstrumentStatus.INSTRUMENT_STATUS_BASE;
import static ru.tinkoff.piapi.core.utils.MapperUtils.mapUnitsAndNanos;

@Service
public class HistoricalCandleMarketServiceImpl implements HistoricalCandleMarketService {

    private static final Logger log = LoggerFactory.getLogger(HistoricalCandleMarketServiceImpl.class);

    private static final int THREE_MINUTES = 3;
    private static final int ONE_HOUR_IN_MINUTES = 60;
    private static final int ONE_DAY_IN_MINUTES = 1440;
    private static final int THREE_DAY_IN_MINUTES = 4320;
    private static final int NUM_OF_ATTEMPTS_TO_GET_CANDLES = 30;
    private static final String CANDLE_FETCH_MESSAGE = "Получение свечи с figi {} за последние минут {}";
    private static final String ERROR_SEARCH_CANDLE = "За промежуток времени: {}, предыдущая свеча figi {} - не найдена";

    private final ShareServiceImpl shareService;

    private final MarketDataService marketDataService;
    private final InstrumentsService instrumentsService;


    @Autowired
    public HistoricalCandleMarketServiceImpl(
            ConnectTApiInvest apiInvest,
            ShareServiceImpl shareService
    ) {
        this.instrumentsService = apiInvest.getInvestApi().getInstrumentsService();
        this.marketDataService = apiInvest.getInvestApi().getMarketDataService();
        this.shareService = shareService;
    }

    public List<CustomCandle> getLastTwoDaysCandle(String figi) {
        return getLastTwoCandleByInterval(figi, THREE_DAY_IN_MINUTES, CANDLE_INTERVAL_DAY);
    }

    public List<CustomCandle> getLastTwoMinuteCandle(String figi) {
        return getLastTwoCandleByInterval(figi, THREE_MINUTES, CANDLE_INTERVAL_1_MIN);
    }

    public List<CustomCandle> getMinuteCandlesForLastHourByFigi(
            final String figi) {
        log.info(CANDLE_FETCH_MESSAGE, figi, ONE_HOUR_IN_MINUTES);
        return fetchIntervalCandlesByFigiAndTimeAndInterval(figi, ONE_HOUR_IN_MINUTES, CANDLE_INTERVAL_1_MIN);
    }

    public Map<String, List<CustomCandle>> getMinuteCandlesForLastDayAllFigis() {
        log.info(CANDLE_FETCH_MESSAGE, "Всех свечей", ONE_DAY_IN_MINUTES);
        return shareService.getAllFigis()
                .stream()
                .collect(Collectors.toMap(
                        figi -> figi,
                        figi -> fetchIntervalCandlesByFigiAndTimeAndInterval(figi, ONE_DAY_IN_MINUTES, CANDLE_INTERVAL_1_MIN)
                ));
    }

    private List<CustomCandle> getLastTwoCandleByInterval(
            String figi,
            int durationMinutesBack,
            CandleInterval interval
    ) {
        Set<CustomCandle> candles = new LinkedHashSet<>();

        for (int attempt = 1; attempt < NUM_OF_ATTEMPTS_TO_GET_CANDLES + 1; attempt++) {
            fetchIntervalCandlesByFigiAndTimeAndInterval(
                    figi,
                    durationMinutesBack * attempt,
                    interval
            )
                    .stream()
                    .sorted(Comparator.comparing(CustomCandle::time, Comparator.reverseOrder()))
                    .limit(2)
                    .forEach(candles::add);

            if (candles.size() == 2) {
                break;
            } else if (candles.size() > 3) {
                candles = candles
                        .stream()
                        .sorted(Comparator.comparing(CustomCandle::time, Comparator.reverseOrder()))
                        .limit(2)
                        .collect(Collectors.toSet());
            }
        }

        if (candles.size() == 1) {
            log.warn(ERROR_SEARCH_CANDLE, durationMinutesBack * NUM_OF_ATTEMPTS_TO_GET_CANDLES, figi);
            throw new RuntimeException("За промежуток времени предыдущая свеча figi - не найдена");
        }

        return new LinkedList<>(candles);
    }

    private List<CustomCandle> fetchIntervalCandlesByFigiAndTimeAndInterval(String figi, int minutesBack, CandleInterval interval) {
        return getIntervalCandlesForTimeByFigi(figi, minutesBack, interval);
    }

    private List<CustomCandle> getIntervalCandlesForTimeByFigi(
            final String figi,
            final int minutesBack,
            final CandleInterval interval
    ) {
        try {
            return marketDataService.getCandlesSync(
                            figi,
                            Instant.now().minus(minutesBack, ChronoUnit.MINUTES),
                            Instant.now(),
                            interval
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
