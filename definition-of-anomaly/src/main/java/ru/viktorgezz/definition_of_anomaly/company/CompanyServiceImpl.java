package ru.viktorgezz.definition_of_anomaly.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyDao companyDao;

    @Autowired
    public CompanyServiceImpl(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    @Override
    public boolean isCompanyPresent(String figi) {
        return companyDao.getIdCompanyByFigi(figi) != 0;
    }

    @Override
    @Transactional
    public void save(
            String figi,
            String name,
            String ticker
    ) {
        companyDao.save(figi, name, ticker);
    }

    @Override
    public long getIdCompanyByFigi(String figi) {
        return companyDao.getIdCompanyByFigi(figi);
    }

    @Override
    public String getNameCompanyByFigi(String figi) {
        return companyDao.getNameCompanyByFigi(figi);
    }

    @Override
    public List<Long> getIdsCompany() {
        return companyDao.getIdsCompany();
    }

    @Override
    public String getTickerByFigi(String figi) {
        return companyDao.getTickerByFigi(figi);
    }
}
