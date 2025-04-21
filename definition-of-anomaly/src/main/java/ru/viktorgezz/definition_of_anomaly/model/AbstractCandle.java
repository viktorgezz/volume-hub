package ru.viktorgezz.definition_of_anomaly.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

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
