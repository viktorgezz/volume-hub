package ru.viktorgezz.definition_of_anomaly.company.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.definition_of_anomaly.company.Company;
import ru.viktorgezz.definition_of_anomaly.company.intf.CompanyUpdateRepo;

@Service
public class CompanyUpdateServiceImpl implements CompanyUpdateService {

    private final CompanyUpdateRepo companyUpdateRepo;

    @Autowired
    public CompanyUpdateServiceImpl(CompanyUpdateRepo companyUpdateRepo) {
        this.companyUpdateRepo = companyUpdateRepo;
    }

    @Override
    @Transactional
    public void update(final Company company, final String lookupTicker) {
        if (company.getFigi() != null && !company.getFigi().isEmpty()) {
            companyUpdateRepo.updateFigiByTicker(company.getFigi(), lookupTicker);
        } else if (company.getName() != null && !company.getName().isEmpty()) {
            companyUpdateRepo.updateNameByTicker(company.getName(), lookupTicker);
        } else if (company.getTicker()!= null && !company.getTicker().isEmpty()) {
            companyUpdateRepo.updateTickerByTicker(company.getTicker(), lookupTicker);
        }
    }
}
