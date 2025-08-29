package ru.viktorgezz.definition_of_anomaly.company;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("company")
public class Company {

    @Id
    private Long id;

    private String name;

    private String ticker;

    private String figi;

    public Company() {
    }

    public Company(
            String name,
            String ticker,
            String figi
    ) {
        this.name = name;
        this.ticker = ticker;
        this.figi = figi;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Company company = (Company) object;
        return Objects.equals(id, company.id) && Objects.equals(name, company.name) && Objects.equals(ticker, company.ticker) && Objects.equals(figi, company.figi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, ticker, figi);
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ticker='" + ticker + '\'' +
                ", figi='" + figi + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getFigi() {
        return figi;
    }

    public void setFigi(String figi) {
        this.figi = figi;
    }
}
