package ru.viktorgezz.api_T_connector.service.impl;

import org.springframework.stereotype.Component;
import ru.viktorgezz.api_T_connector.service.interf.CompanyService;
import ru.viktorgezz.api_T_connector.util.ShareDao;

@Component
public class CompanyServiceImpl implements CompanyService {

    private final ShareDao shareDao;

    public CompanyServiceImpl(ShareDao shareDao) {
        this.shareDao = shareDao;
    }

    public String getCompanyNameByFigi(String figi) {
        return shareDao.getCompanyNameByFigi(figi);
    }
}
