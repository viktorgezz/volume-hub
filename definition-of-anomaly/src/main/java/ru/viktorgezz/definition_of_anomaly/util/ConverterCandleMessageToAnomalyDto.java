package ru.viktorgezz.definition_of_anomaly.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.client.ClientRecipientInvest;
import ru.viktorgezz.definition_of_anomaly.dao.CompanyDao;
import ru.viktorgezz.definition_of_anomaly.dto.CandleAnomalyDto;
import ru.viktorgezz.definition_of_anomaly.dto.CandleDto;
import ru.viktorgezz.definition_of_anomaly.dto.CandleMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class ConverterCandleMessageToAnomalyDto {

    private static final Logger log = LoggerFactory.getLogger(ConverterCandleMessageToAnomalyDto.class);
    private final ClientRecipientInvest clientRecipientInvest;
    private final CompanyDao companyDao;

    @Autowired
    public ConverterCandleMessageToAnomalyDto(
            ClientRecipientInvest clientRecipientInvest,
            CompanyDao companyDao
    ) {
        this.clientRecipientInvest = clientRecipientInvest;
        this.companyDao = companyDao;
    }

    public CandleAnomalyDto convertToCandleAnomalyDto(CandleMessage candle) {
        final String figi = candle.getFigi();

        try {
            return new CandleAnomalyDto.Builder()
                    .setName(companyDao.getNameCompanyByFigi(figi))
                    .setPriceCurrent(candle.getClose())
                    .setVolume(candle.getVolume())
                    .setPriceDailyChangeAsPercentage(
                            calculatePriceChangeAsPercentage(clientRecipientInvest.fetchLastTwoDaysCandle(figi))
                    )
                    .setPriceMinuteChangeAsPercentage(
                            calculatePriceChangeAsPercentage(clientRecipientInvest.fetchMinuteCandlesForLastMinute(figi))
                    )
                    .setTime(candle.getTime())
                    .setCandlesLastHour(clientRecipientInvest.fetchMinuteCandlesForLastHour(figi))
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
