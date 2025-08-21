package ru.viktorgezz.definition_of_anomaly.company;

import java.util.Optional;

public interface CompanyClient {

    Optional<CompanyRsDto> fetchCompanyByFigi(String figi);
}
