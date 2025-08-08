package ru.viktorgezz.definition_of_anomaly.candle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.definition_of_anomaly.candle.CandleDao;
import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleDto;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleStatisticsService;
import ru.viktorgezz.definition_of_anomaly.metric.Metric;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CandleStatisticsServiceImpl implements CandleStatisticsService {

    private final CandleDao candleDao;

    @Autowired
    public CandleStatisticsServiceImpl(CandleDao candleDao) {
        this.candleDao = candleDao;
    }


    @Override
    @Transactional
    public void saveCandles(List<CandleDto> candleDtos, long idCompany) {
        candleDao.saveCandles(candleDtos, idCompany);
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
