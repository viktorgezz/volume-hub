package ru.viktorgezz.definition_of_anomaly.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.viktorgezz.definition_of_anomaly.client.ClientSenderAnomalyCandle;
import ru.viktorgezz.definition_of_anomaly.dao.CompanyDao;
import ru.viktorgezz.definition_of_anomaly.dao.MetricDao;
import ru.viktorgezz.definition_of_anomaly.dto.CandleMessage;
import ru.viktorgezz.definition_of_anomaly.service.interf.CandleAnomalousService;

@Service
public class CandleAnomalousServiceImpl implements CandleAnomalousService {

    private static final Logger log = LoggerFactory.getLogger(CandleAnomalousServiceImpl.class);
    private final MetricDao metricDao;
    private final CompanyDao companyDao;
    private final ClientSenderAnomalyCandle sender;

    @Autowired
    public CandleAnomalousServiceImpl(
            MetricDao metricDao,
            CompanyDao companyDao,
            ClientSenderAnomalyCandle sender) {
        this.metricDao = metricDao;
        this.companyDao = companyDao;
        this.sender = sender;
    }

    public void foundAnomalyCandle(CandleMessage candle) {
        if (isAnomaly(candle)) {
            log.info("Найден аномальный объем!!! figi: {}, volume: {}, time: {}",
                    candle.getFigi(), candle.getVolume(), candle.getTime());

            sender.send(candle);
        }
    }

    private boolean isAnomaly(CandleMessage candle) {
        final long idCompany = companyDao.getIdCompanyByFigi(candle.getFigi());
        return metricDao
                .calculateCurrentZScore(candle.getVolume(), idCompany)
                .compareTo(metricDao.getZScoreFromTable(idCompany))
                >= 1;
    }
}
