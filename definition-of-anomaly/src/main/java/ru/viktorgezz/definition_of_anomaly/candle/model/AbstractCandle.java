package ru.viktorgezz.definition_of_anomaly.candle.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

public abstract class AbstractCandle {
    protected BigDecimal open;
    protected BigDecimal close;
    protected BigDecimal high;
    protected BigDecimal low;
    protected long volume;
    protected Timestamp time;

    public AbstractCandle() {
    }

    public AbstractCandle(BigDecimal open, BigDecimal close, BigDecimal high, BigDecimal low, long volume, Timestamp time) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.time = time;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AbstractCandle that = (AbstractCandle) object;
        return volume == that.volume && Objects.equals(open, that.open) && Objects.equals(close, that.close) && Objects.equals(high, that.high) && Objects.equals(low, that.low) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(open, close, high, low, volume, time);
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public long getVolume() {
        return volume;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
