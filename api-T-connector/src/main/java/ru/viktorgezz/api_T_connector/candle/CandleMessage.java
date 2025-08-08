package ru.viktorgezz.api_T_connector.candle;

import java.math.BigDecimal;
import java.util.Objects;

public class CandleMessage {

    private String figi;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal high;
    private BigDecimal low;
    private long volume;
    private String time;

    public CandleMessage() {
    }

    public CandleMessage(String figi, BigDecimal open, BigDecimal close, BigDecimal high, BigDecimal low, long volume, String time) {
        this.figi = figi;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.time = time;
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
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public String getFigi() {
        return figi;
    }

    public void setFigi(String figi) {
        this.figi = figi;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
