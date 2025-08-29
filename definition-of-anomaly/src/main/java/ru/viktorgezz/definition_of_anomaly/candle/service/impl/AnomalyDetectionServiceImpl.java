package ru.viktorgezz.definition_of_anomaly.candle.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.viktorgezz.definition_of_anomaly.candle.model.CandleMessage;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.AnomalyDetectionService;
import ru.viktorgezz.definition_of_anomaly.metric.StandardDeviationAndCriticalValue;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.MetricDynamicService;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.MetricService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AnomalyDetectionServiceImpl implements AnomalyDetectionService {
    private static final String COMPANY_NOT_FOUND = "Компания не была найдена в бд figi: {}, volume: {}, time: {}";
    private static final Logger log = LoggerFactory.getLogger(AnomalyDetectionServiceImpl.class);

    private final MetricService metricService;
    private final MetricDynamicService metricDynamicService;

    @Autowired
    public AnomalyDetectionServiceImpl(
            MetricService metricService,
            MetricDynamicService metricDynamicService
    ) {
        this.metricService = metricService;
        this.metricDynamicService = metricDynamicService;
    }

    public boolean isAnomaly(CandleMessage candle, final long idCompany) {
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

        log.debug(
                "figi: {}, Критерий ирвина: {}, абсолютная разница: {}, критическое значение: {} стандартное отклонение: {} \n Сравнение: {}",
                candle.getFigi(), criterionIrvina, absoluteDifference, criticalValueWithCriterionOfRelevance, standardDeviation, criterionIrvina.compareTo(criticalValueWithCriterionOfRelevance) == 1);

        return criterionIrvina.compareTo(criticalValueWithCriterionOfRelevance) > 0;
    }
}
