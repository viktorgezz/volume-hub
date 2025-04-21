package ru.viktorgezz.definition_of_anomaly.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class CandleAnomalyDto {

    private String name;
    private BigDecimal priceCurrent;
    private long volume;
    private BigDecimal priceDailyChangeAsPercentage;
    private BigDecimal priceMinuteChangeAsPercentage;
    private Timestamp time;

    private CandleAnomalyDto(Builder builder) {
        this.name = builder.name;
        this.priceCurrent = builder.priceCurrent;
        this.volume = builder.volume;
        this.priceDailyChangeAsPercentage = builder.priceDailyChangeAsPercentage;
        this.priceMinuteChangeAsPercentage = builder.priceMinuteChangeAsPercentage;
        this.time = builder.time;
    }

    @Override
    public String toString() {
        return "CandleAnomalyDto{" +
                "name='" + name + '\'' +
                ", priceCurrent=" + priceCurrent +
                ", volume=" + volume +
                ", priceDailyChangeAsPercentage=" + priceDailyChangeAsPercentage +
                ", priceMinuteChangeAsPercentage=" + priceMinuteChangeAsPercentage +
                ", time=" + time +
                '}';
    }

    public static class Builder {
        private String name;
        private BigDecimal priceCurrent;
        private long volume;
        private BigDecimal priceDailyChangeAsPercentage;
        private BigDecimal priceMinuteChangeAsPercentage;
        private Timestamp time;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPriceCurrent(BigDecimal priceCurrent) {
            this.priceCurrent = priceCurrent;
            return this;
        }

        public Builder setVolume(long volume) {
            this.volume = volume;
            return this;
        }

        public Builder setPriceDailyChangeAsPercentage(BigDecimal priceDailyChangeAsPercentage) {
            this.priceDailyChangeAsPercentage = priceDailyChangeAsPercentage;
            return this;
        }

        public Builder setPriceMinuteChangeAsPercentage(BigDecimal priceMinuteChangeAsPercentage) {
            this.priceMinuteChangeAsPercentage = priceMinuteChangeAsPercentage;
            return this;
        }

        public Builder setTime(Timestamp time) {
            this.time = time;
            return this;
        }

        public CandleAnomalyDto build() {
            return new CandleAnomalyDto(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPriceCurrent() {
        return priceCurrent;
    }

    public void setPriceCurrent(BigDecimal priceCurrent) {
        this.priceCurrent = priceCurrent;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public BigDecimal getPriceDailyChangeAsPercentage() {
        return priceDailyChangeAsPercentage;
    }

    public void setPriceDailyChangeAsPercentage(BigDecimal priceDailyChangeAsPercentage) {
        this.priceDailyChangeAsPercentage = priceDailyChangeAsPercentage;
    }

    public BigDecimal getPriceMinuteChangeAsPercentage() {
        return priceMinuteChangeAsPercentage;
    }

    public void setPriceMinuteChangeAsPercentage(BigDecimal priceMinuteChangeAsPercentage) {
        this.priceMinuteChangeAsPercentage = priceMinuteChangeAsPercentage;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
