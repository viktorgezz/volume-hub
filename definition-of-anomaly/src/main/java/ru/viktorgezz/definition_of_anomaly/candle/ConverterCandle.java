package ru.viktorgezz.definition_of_anomaly.candle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.candle.intf.CandleApiClient;
import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleAnomalyDto;
import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleDto;
import ru.viktorgezz.definition_of_anomaly.candle.model.CandleMessage;
import ru.viktorgezz.definition_of_anomaly.company.CompanyService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

@Component
public class ConverterCandle {

    private static final Logger log = LoggerFactory.getLogger(ConverterCandle.class);

    private final CandleApiClient candleApiClient;
    private final CompanyService companyService;

    @Autowired
    public ConverterCandle(
            CandleApiClient candleApiClient,
            CompanyService companyService
    ) {
        this.candleApiClient = candleApiClient;
        this.companyService = companyService;
    }

    public CandleAnomalyDto convertToCandleAnomalyDto(CandleMessage candle) {
        final String figi = candle.getFigi();
        final int three_hours = 3 * 60 * 60 * 1000;

        try {
            Timestamp time = new Timestamp(candle.getTime().getTime() + three_hours);

            CandleDto converterCandle = convertToCandleDto(candle);

            LinkedHashSet<CandleDto> minuteCandlesForLastHour = new LinkedHashSet<>(candleApiClient.fetchMinuteCandlesForLastHour(figi));
            List<CandleDto> lastTwoCandle = List.of(minuteCandlesForLastHour.getLast(), converterCandle);
            minuteCandlesForLastHour.addLast(converterCandle);

            return new CandleAnomalyDto.Builder()
                    .setName(companyService.getNameCompanyByFigi(figi))
                    .setTicker(companyService.getTickerByFigi(figi))
                    .setPriceCurrent(candle.getClose())
                    .setVolume(candle.getVolume())
                    .setPriceDailyChangeAsPercentage(
                            calculatePriceChangeAsPercentage(candleApiClient
                                    .fetchLastTwoDaysCandle(figi)
                            )
                    )
                    .setPriceMinuteChangeAsPercentage(
                            calculatePriceChangeAsPercentage(lastTwoCandle)
                    )
                    .setTime(time)
                    .setCandlesLastHour(minuteCandlesForLastHour)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private BigDecimal calculatePriceChangeAsPercentage(List<CandleDto> candles) {
        final BigDecimal priceCloseCurr = candles.getLast().getClose();
        final BigDecimal priceCloseLast = candles.getFirst().getClose();
        final BigDecimal priceChange = priceCloseCurr.subtract(priceCloseLast);

        log.info("closeCurr: {}, closeLastDay: {}, change: {}",
                priceCloseCurr, priceCloseLast, priceChange);

        return priceChange
                .divide(priceCloseLast, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private CandleDto convertToCandleDto(CandleMessage candleMessage) {
        return new CandleDto(
                candleMessage.getOpen(),
                candleMessage.getClose(),
                candleMessage.getHigh(),
                candleMessage.getLow(),
                candleMessage.getVolume(),
                candleMessage.getTime()
        );
    }
}
