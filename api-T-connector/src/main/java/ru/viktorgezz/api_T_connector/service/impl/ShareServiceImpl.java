package ru.viktorgezz.api_T_connector.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.viktorgezz.api_T_connector.model.Share;
import ru.viktorgezz.api_T_connector.service.interf.ShareService;
import ru.viktorgezz.api_T_connector.dao.ShareDao;

import java.util.List;
import java.util.Optional;

@Component
public class ShareServiceImpl implements ShareService {

    private static final Logger log = LoggerFactory.getLogger(ShareServiceImpl.class);
    private List<String> figis;

    private final ShareDao shareDao;

    @Autowired
    public ShareServiceImpl(ShareDao shareDao) {
        this.shareDao = shareDao;
        importCsvToFigisTable();
    }

    @Override
    public Optional<String> importCsvToFigisTable() {
        Optional<String> messageOpt = shareDao.importCsvToFigisTable();
        this.figis = getFigisFromDb();
        return messageOpt;
    }

    @Override
    public List<String> getAllFigis() {
        return figis;
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

    private List<String> getFigisFromDb() {
        return shareDao.getAllFigis();

    }
}
