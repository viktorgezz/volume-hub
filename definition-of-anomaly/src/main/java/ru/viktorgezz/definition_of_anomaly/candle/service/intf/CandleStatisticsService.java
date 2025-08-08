package ru.viktorgezz.definition_of_anomaly.candle.service.intf;

import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleDto;
import ru.viktorgezz.definition_of_anomaly.metric.Metric;

import java.math.BigDecimal;
import java.util.List;

public interface CandleStatisticsService {

    void saveCandles(List<CandleDto> candleDtos, long idCompany);

    BigDecimal computeCriticalValue(Long idCompany);

    Metric computeStandardDeviationAndAverage(Long idCompany);

}
