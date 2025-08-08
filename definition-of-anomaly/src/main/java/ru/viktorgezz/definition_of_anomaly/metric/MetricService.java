package ru.viktorgezz.definition_of_anomaly.metric;

import java.math.BigDecimal;

public interface MetricService {

    void save(Long idCompany, Metric statsMetric);

    void save(Long idCompany, MetricByIrvin statsMetric);

    BigDecimal computeZScore(long volume, long idCompany);

    BigDecimal computeAbsoluteDifference(Long idCompany, Long volume);

    BigDecimal getZScoreFromTable(long idCompany);

    StandardDeviationAndCriticalValue getStandardDeviationAndCriticalValue(long idCompany);
}
