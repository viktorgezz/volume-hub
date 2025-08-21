package ru.viktorgezz.definition_of_anomaly.metric.service.intrf;

import ru.viktorgezz.definition_of_anomaly.metric.model.MetricByIrvin;
import ru.viktorgezz.definition_of_anomaly.metric.StandardDeviationAndCriticalValue;

import java.math.BigDecimal;

public interface MetricService {

    void save(Long idCompany, MetricByIrvin statsMetric);

    BigDecimal computeAbsoluteDifference(Long idCompany, Long volume);

    StandardDeviationAndCriticalValue getStandardDeviationAndCriticalValue(long idCompany);
}
