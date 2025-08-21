package ru.viktorgezz.definition_of_anomaly.candle.service.intf;

import ru.viktorgezz.definition_of_anomaly.metric.model.Metric;

import java.math.BigDecimal;

public interface CandleStatisticsService {

    BigDecimal computeCriticalValue(Long idCompany);

    Metric computeStandardDeviationAndAverage(Long idCompany);

}
