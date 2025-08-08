package ru.viktorgezz.definition_of_anomaly.company;

import java.util.List;

public interface CompanyService {

    boolean isCompanyPresent(String figi);

    void save(String figi, String name, String ticker);

    long getIdCompanyByFigi(String figi);

    String getNameCompanyByFigi(String figi);

    List<Long> getIdsCompany();

    String getTickerByFigi(String figi);

}
