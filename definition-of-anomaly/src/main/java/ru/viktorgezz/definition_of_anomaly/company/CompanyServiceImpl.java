package ru.viktorgezz.definition_of_anomaly.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private static final String COMPANY_ADDED_WITH_FIGI_AND_NAME = "figi: {} and name: {} add in Company, ticker: {}";
    private static final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private final CompanyDao companyDao;
    private final CompanyClient companyClient;

    @Autowired
    public CompanyServiceImpl(
            CompanyDao companyDao,
            CompanyClient companyClient
    ) {
        this.companyDao = companyDao;
        this.companyClient = companyClient;
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

    @Override
    @Transactional
    public void loadCompanyIfNotPresent(String figi) {
        if (!isCompanyPresent(figi)) {
            CompanyRsDto company = companyClient.fetchCompanyByFigi(figi).orElseThrow();
            log.info(COMPANY_ADDED_WITH_FIGI_AND_NAME, figi, company.getName(), company.getTicker());
            save(figi, company.getName(), company.getTicker());
        }
    }
}
