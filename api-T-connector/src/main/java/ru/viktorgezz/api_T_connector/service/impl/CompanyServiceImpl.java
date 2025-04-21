package ru.viktorgezz.api_T_connector.service.impl;

import org.springframework.stereotype.Component;
import ru.viktorgezz.api_T_connector.model.Share;
import ru.viktorgezz.api_T_connector.service.interf.CompanyService;
import ru.viktorgezz.api_T_connector.dao.ShareDao;

import java.util.List;

@Component
public class CompanyServiceImpl implements CompanyService {

    private final ShareDao shareDao;

    public CompanyServiceImpl(ShareDao shareDao) {
        this.shareDao = shareDao;
    }

    public String getCompanyNameByFigi(String figi) {
        return shareDao.getCompanyNameByFigi(figi);
    }

    @Override
    public String getTickerByFigi(String figi) {
        return shareDao.getTickerByFigi(figi);
    }

    @Override
    public List<Share> getAllShare() {
        return shareDao.getAllShare();
    }

    @Override
    public List<String> getAllFigis() {
        return shareDao.getAllFigis();
    }

}
