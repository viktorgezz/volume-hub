package ru.viktorgezz.definition_of_anomaly.company;

public interface CompanyClient {

    CompanyRsDto fetchCompanyByFigi(String figi);
}
