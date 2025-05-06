package ru.viktorgezz.definition_of_anomaly.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.client.ClientInvest;
import ru.viktorgezz.definition_of_anomaly.dao.CompanyDao;
import ru.viktorgezz.definition_of_anomaly.dto.CandleAnomalyDto;
import ru.viktorgezz.definition_of_anomaly.dto.CandleMessage;
import ru.viktorgezz.definition_of_anomaly.model.AbstractCandle;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ConverterCandleMessageToAnomalyDto {

    private static final Logger log = LoggerFactory.getLogger(ConverterCandleMessageToAnomalyDto.class);
    private final ClientInvest clientInvest;
    private final CompanyDao companyDao;

    @Autowired
    public ConverterCandleMessageToAnomalyDto(
            ClientInvest clientInvest,
            CompanyDao companyDao
    ) {
        this.clientInvest = clientInvest;
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
                            calculatePriceChangeAsPercentage(clientInvest.fetchDayCandleCurr(figi))
                    )
                    .setPriceMinuteChangeAsPercentage(
                            calculatePriceChangeAsPercentage(candle)
                    )
                    .setTime(candle.getTime())
                    .setCandlesLastHour(clientInvest.fetchMinuteCandlesForLastHour(figi))
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            System.exit(500);
            return null;
        }
    }

    private BigDecimal calculatePriceChangeAsPercentage(AbstractCandle candle) {
        final BigDecimal priceChange = candle.getClose().subtract(candle.getOpen());
        return priceChange
                .divide(candle.getOpen(), RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(3, RoundingMode.HALF_UP);
    }
}
