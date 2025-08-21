package ru.viktorgezz.definition_of_anomaly.candle.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.viktorgezz.definition_of_anomaly.candle.intf.SenderAnomalyCandle;
import ru.viktorgezz.definition_of_anomaly.candle.model.CandleMessage;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleAnomalousService;
import ru.viktorgezz.definition_of_anomaly.company.CompanyService;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.MetricDynamicService;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.MetricService;
import ru.viktorgezz.definition_of_anomaly.metric.StandardDeviationAndCriticalValue;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CandleAnomalousByIrvinServiceImpl implements CandleAnomalousService {

    private static final Logger log = LoggerFactory.getLogger(CandleAnomalousByIrvinServiceImpl.class);

    private static final String COMPANY_NOT_FOUND = "Компания не была найдена в бд figi: {}, volume: {}, time: {}";
    private static final String ANOMALOUS_VOLUME_DETECTED = "Найден аномальный объем figi: {}, volume: {}, time: {}";

    private final MetricService metricService;
    private final CompanyService companyService;
    private final MetricDynamicService metricDynamicService;
    private final SenderAnomalyCandle sender;

    @Autowired
    public CandleAnomalousByIrvinServiceImpl(
            MetricService metricService,
            CompanyService companyService,
            MetricDynamicService metricDynamicService,
            SenderAnomalyCandle sender
    ) {
        this.metricService = metricService;
        this.companyService = companyService;
        this.metricDynamicService = metricDynamicService;
        this.sender = sender;
    }

    @Override
    public void foundAnomalyCandle(CandleMessage candle) {
        if (isAnomaly(candle)) {
            log.info(ANOMALOUS_VOLUME_DETECTED, candle.getFigi(), candle.getVolume(), candle.getTime());
            sender.send(candle);
            metricDynamicService.increaseCoefficient(companyService.getIdCompanyByFigi(candle.getFigi()));
        }
    }


    private boolean isAnomaly(CandleMessage candle) {
        final long idCompany = companyService.getIdCompanyByFigi(candle.getFigi());

        if (idCompany == 0) {
            log.warn(COMPANY_NOT_FOUND, candle.getFigi(), candle.getVolume(), candle.getTime());
            return false;
        }

        BigDecimal absoluteDifference = metricService.computeAbsoluteDifference(
                idCompany, candle.getVolume());

        if (absoluteDifference.compareTo(BigDecimal.ZERO) == -1) {
            return false;
        }

        if (absoluteDifference.compareTo(BigDecimal.ZERO) == 0) {
            log.debug("Абсолютная разница равна: 0. Figi: {}", candle.getFigi());
            return false;
        }

        StandardDeviationAndCriticalValue stndAndCritVal = metricService.getStandardDeviationAndCriticalValue(idCompany);
        BigDecimal standardDeviation = stndAndCritVal.StandardDeviation();
        BigDecimal criticalValueWithCriterionOfRelevance = stndAndCritVal
                .CriticalValue()
                .multiply(metricDynamicService
                        .findCoefficientByIdCompany(idCompany)
                        .orElseThrow().getCoefficient());


        BigDecimal criterionIrvina = absoluteDifference
                .divide(
                        standardDeviation,
                        RoundingMode.HALF_UP);

        log.info(
                "figi: {}, Критерий ирвина: {}, абсолютная разница: {}, критическое значение: {} стандартное отклонение: {} \n Сравнение: {}",
                candle.getFigi(), criterionIrvina, absoluteDifference, criticalValueWithCriterionOfRelevance, standardDeviation, criterionIrvina.compareTo(criticalValueWithCriterionOfRelevance) == 1);

        return criterionIrvina.compareTo(criticalValueWithCriterionOfRelevance) > 0;
    }

}
