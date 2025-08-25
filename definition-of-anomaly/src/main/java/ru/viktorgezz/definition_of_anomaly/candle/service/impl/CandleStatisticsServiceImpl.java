package ru.viktorgezz.definition_of_anomaly.candle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.viktorgezz.definition_of_anomaly.candle.CandleDao;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleStatisticsService;
import ru.viktorgezz.definition_of_anomaly.metric.model.Metric;

import java.math.BigDecimal;

@Service
public class CandleStatisticsServiceImpl implements CandleStatisticsService {

    private final CandleDao candleDao;

    @Autowired
    public CandleStatisticsServiceImpl(CandleDao candleDao) {
        this.candleDao = candleDao;
    }

    @Override
    public BigDecimal computeCriticalValue(Long idCompany) {
        return candleDao.computeCriticalValue(idCompany);
    }

    @Override
    public Metric computeStandardDeviationAndAverage(Long idCompany) {
        return candleDao.computeStandardDeviationAndAverage(idCompany);
    }
}
