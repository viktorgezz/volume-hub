package ru.viktorgezz.definition_of_anomaly.company.intf;

import ru.viktorgezz.definition_of_anomaly.company.CompanyRsDto;

import java.util.Optional;

public interface CompanyApiClient {

    Optional<CompanyRsDto> fetchCompanyByFigi(String figi);
}
