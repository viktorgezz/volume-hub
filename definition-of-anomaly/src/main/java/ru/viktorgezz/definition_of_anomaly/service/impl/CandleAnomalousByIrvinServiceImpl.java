package ru.viktorgezz.definition_of_anomaly.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.viktorgezz.definition_of_anomaly.client.ClientSenderAnomalyCandle;
import ru.viktorgezz.definition_of_anomaly.dao.CandleDao;
import ru.viktorgezz.definition_of_anomaly.dao.CompanyDao;
import ru.viktorgezz.definition_of_anomaly.dao.MetricDao;
import ru.viktorgezz.definition_of_anomaly.dto.CandleMessage;
import ru.viktorgezz.definition_of_anomaly.service.interf.CandleAnomalousService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CandleAnomalousByIrvinServiceImpl implements CandleAnomalousService {

    private static final Logger log = LoggerFactory.getLogger(CandleAnomalousByIrvinServiceImpl.class);

    private static final String COMPANY_NOT_FOUND = "Компания не была найдена в бд figi: {}, volume: {}, time: {}";
    private static final String ANOMALOUS_VOLUME_DETECTED = "Найден аномальный объем figi: {}, volume: {}, time: {}";

    private final CompanyDao companyDao;
    private final MetricDao metricDao;
    private final ClientSenderAnomalyCandle sender;

    @Autowired
    public CandleAnomalousByIrvinServiceImpl(
            CompanyDao companyDao,
            CandleDao candleDao,
            MetricDao metricDao,
            ClientSenderAnomalyCandle sender
    ) {
        this.companyDao = companyDao;
        this.metricDao = metricDao;
        this.sender = sender;
    }

    @Override
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

        BigDecimal absoluteDifference = metricDao.calculateAbsoluteDifference(
                idCompany, candle.getVolume());

        if (absoluteDifference.compareTo(BigDecimal.ZERO) == -1) {
            log.info("Абсолютная разница меньше нуля: {}. Figi: {}", absoluteDifference, candle.getFigi());
            return false;
        }

        if (absoluteDifference.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Абсолютная разница равна: 0. Figi: {}", candle.getFigi());
            return false;
        }

        BigDecimal standardDeviation = metricDao.getStandardDeviation(idCompany);
        BigDecimal criticalValue = metricDao.getCriticalValue(idCompany);


        BigDecimal criterionIrvina = absoluteDifference
                .divide(
                        standardDeviation,
                        RoundingMode.HALF_UP);

        log.info(
                "figi: {}, Критерий ирвина: {}, абсолютная разница: {}, критическое значение: {} стандартное отклонение: {}",
                candle.getFigi(), criterionIrvina, absoluteDifference, criticalValue, standardDeviation);
        log.info("Сравнение: {}", criterionIrvina.compareTo(criticalValue) == 1);

        return criterionIrvina.compareTo(criticalValue) == 1;
    }

}
