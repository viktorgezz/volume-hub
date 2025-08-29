package ru.viktorgezz.definition_of_anomaly.company.service;

import ru.viktorgezz.definition_of_anomaly.company.Company;

public interface CompanyUpdateService {

    void update(Company company, String lookupTicker);
}
