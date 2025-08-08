package ru.viktorgezz.definition_of_anomaly.candle.dto;

import ru.viktorgezz.definition_of_anomaly.candle.AbstractCandle;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class CandleDto extends AbstractCandle {

    public CandleDto() {
    }

    public CandleDto(
            BigDecimal open,
            BigDecimal close,
            BigDecimal high,
            BigDecimal low,
            long volume,
            Timestamp time) {
        super(open, close, high, low, volume, time);
    }



    @Override
    public String toString() {
        return "CandleDto{" +
                "open=" + open +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                ", time=" + time +
                '}';
    }
}
