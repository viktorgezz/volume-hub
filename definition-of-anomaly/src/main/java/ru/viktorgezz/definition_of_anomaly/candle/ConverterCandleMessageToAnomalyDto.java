package ru.viktorgezz.definition_of_anomaly.candle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.candle.intf.CandleDataClient;
import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleAnomalyDto;
import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleDto;
import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleMessageDto;
import ru.viktorgezz.definition_of_anomaly.company.CompanyService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class ConverterCandleMessageToAnomalyDto {

    private static final Logger log = LoggerFactory.getLogger(ConverterCandleMessageToAnomalyDto.class);

    private final CandleDataClient candleDataClient;
    private final CompanyService companyService;

    @Autowired
    public ConverterCandleMessageToAnomalyDto(
            CandleDataClient candleDataClient,
            CompanyService companyService
            ) {
        this.candleDataClient = candleDataClient;
        this.companyService = companyService;
    }

    public CandleAnomalyDto convertToCandleAnomalyDto(CandleMessageDto candle) {
        final String figi = candle.getFigi();

        try {
            return new CandleAnomalyDto.Builder()
                    .setName(companyService.getNameCompanyByFigi(figi))
                    .setTicker(companyService.getTickerByFigi(figi))
                    .setPriceCurrent(candle.getClose())
                    .setVolume(candle.getVolume())
                    .setPriceDailyChangeAsPercentage(
                            calculatePriceChangeAsPercentage(candleDataClient.fetchLastTwoDaysCandle(figi))
                    )
                    .setPriceMinuteChangeAsPercentage(
                            calculatePriceChangeAsPercentage(candleDataClient.fetchMinuteCandlesForLastMinute(figi))
                    )
                    .setTime(candle.getTime())
                    .setCandlesLastHour(candleDataClient.fetchMinuteCandlesForLastHour(figi))
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            System.exit(500);
            return null;
        }
    }

    private BigDecimal calculatePriceChangeAsPercentage(List<CandleDto> candles) {
        final BigDecimal priceCloseCurr = candles.get(candles.size() - 2).getClose();
        final BigDecimal priceCloseLast = candles.get(candles.size() - 1).getClose();
        final BigDecimal priceChange = priceCloseCurr.subtract(priceCloseLast);

        log.info("closeCurr: {}, closeLastDay: {}, change: {}",
                priceCloseCurr, priceCloseLast, priceChange);

        return priceChange
                .divide(priceCloseLast, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
