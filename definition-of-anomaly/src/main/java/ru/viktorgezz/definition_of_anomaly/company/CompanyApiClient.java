package ru.viktorgezz.definition_of_anomaly.company;

import java.util.Optional;

public interface CompanyApiClient {

    Optional<CompanyRsDto> fetchCompanyByFigi(String figi);
}
