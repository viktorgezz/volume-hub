package ru.viktorgezz.company_info.company.intf;

import ru.viktorgezz.company_info.company.Company;

import java.util.List;

public interface CompanyService {

    List<String> getAllFigis();

    List<Company> getAllCompany();

    String findNameByFigi(String figi);

    Company findCompanyByFigi(String figi);

    Company findCompanyByTicker(String ticker);

    void addCompany(Company company);

    void deleteCompanyByTicker(String ticker);

    void updateFigiByTicker(String figi, String ticker);

    void updateNameByTicker(String name, String ticker);

    void updateTickerByTicker(String oldTicker, String newTicker);
}
