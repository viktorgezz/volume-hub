package ru.viktorgezz.definition_of_anomaly.dto;

public class CompanyRsDto {

    private final String name;

    private final String ticker;

    private final String figi;

    public CompanyRsDto(String name, String ticker, String figi) {
        this.name = name;
        this.ticker = ticker;
        this.figi = figi;
    }

    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public String getFigi() {
        return figi;
    }
}
