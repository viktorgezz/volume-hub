package ru.viktorgezz.api_T_connector.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

public record CustomCandle(
        BigDecimal open,
        BigDecimal close,
        BigDecimal high,
        BigDecimal low,
        long volume,
        Timestamp time
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomCandle candle = (CustomCandle) o;
        return volume == candle.volume && Objects.equals(low, candle.low) && Objects.equals(time, candle.time) && Objects.equals(open, candle.open) && Objects.equals(high, candle.high) && Objects.equals(close, candle.close);
    }

    @Override
    public int hashCode() {
        return Objects.hash(open, close, high, low, volume, time);
    }

}
