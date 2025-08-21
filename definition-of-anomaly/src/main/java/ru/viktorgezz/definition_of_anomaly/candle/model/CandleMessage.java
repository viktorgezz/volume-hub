package ru.viktorgezz.definition_of_anomaly.candle.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

public class CandleMessage extends AbstractCandle {

    private String figi;

    public CandleMessage() {
    }

    public CandleMessage(
            String figi,
            BigDecimal open,
            BigDecimal close,
            BigDecimal high,
            BigDecimal low,
            long volume,
            Timestamp time
    ) {
        super(open, close, high, low, volume, time);
        this.figi = figi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandleMessage that = (CandleMessage) o;
        return Objects.equals(figi, that.figi) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(figi, time);
    }

    public BigDecimal getClose() {
        return super.getClose();
    }

    public String getFigi() {
        return figi;
    }

    public void setFigi(String figi) {
        this.figi = figi;
    }

}
