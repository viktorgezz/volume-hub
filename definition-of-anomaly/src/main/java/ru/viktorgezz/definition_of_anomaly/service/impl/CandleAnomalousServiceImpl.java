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

    private static final String ANOMALOUS_VOLUME_DETECTED = "Найден аномальный объем figi: {}, volume: {}, time: {}";
    private static final String COMPANY_NOT_FOUND = "Компания не была найдена в бд figi: {}, volume: {}, time: {}";

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
            log.info(ANOMALOUS_VOLUME_DETECTED, candle.getFigi(), candle.getVolume(), candle.getTime());
            sender.send(candle);
        }
    }

    private boolean isAnomaly(CandleMessage candle) {
        final long idCompany = companyDao.getIdCompanyByFigi(candle.getFigi());
        if (idCompany == 0) {
            log.warn(COMPANY_NOT_FOUND, candle.getFigi(), candle.getVolume(), candle.getTime());
            return false;
        }
        return metricDao
                .calculateCurrentZScore(candle.getVolume(), idCompany)
                .compareTo(metricDao.getZScoreFromTable(idCompany))
                >= 1;
    }
}
