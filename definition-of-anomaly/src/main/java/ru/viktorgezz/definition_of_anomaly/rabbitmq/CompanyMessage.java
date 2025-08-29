package ru.viktorgezz.definition_of_anomaly.rabbitmq;

import java.util.Objects;

public class CompanyMessage {

    private String name;
    private String tickerUpdate;
    private String figi;

    private String lookupTicker; // Тикер по которому идет поиск для обновления значений

    public CompanyMessage() {
    }

    public CompanyMessage(
            String name,
            String tickerUpdate,
            String figi,
            String lookupTicker
    ) {
        this.name = name;
        this.tickerUpdate = tickerUpdate;
        this.figi = figi;
        this.lookupTicker = lookupTicker;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CompanyMessage that = (CompanyMessage) object;
        return Objects.equals(name, that.name) && Objects.equals(tickerUpdate, that.tickerUpdate) && Objects.equals(figi, that.figi) && Objects.equals(lookupTicker, that.lookupTicker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tickerUpdate, figi, lookupTicker);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTickerUpdate() {
        return tickerUpdate;
    }

    public void setTickerUpdate(String tickerUpdate) {
        this.tickerUpdate = tickerUpdate;
    }

    public String getFigi() {
        return figi;
    }

    public void setFigi(String figi) {
        this.figi = figi;
    }

    public String getLookupTicker() {
        return lookupTicker;
    }

    public void setLookupTicker(String lookupTicker) {
        this.lookupTicker = lookupTicker;
    }
}
