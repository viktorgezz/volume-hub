package ru.viktorgezz.definition_of_anomaly.candle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.definition_of_anomaly.candle.CandleDao;
import ru.viktorgezz.definition_of_anomaly.candle.model.AbstractCandle;
import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleDto;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleService;

import java.util.List;

@Service
public class CandleServiceImpl implements CandleService {

    private final CandleDao candleDao;

    @Autowired
    public CandleServiceImpl(CandleDao candleDao) {
        this.candleDao = candleDao;
    }

    @Override
    @Transactional
    public void saveCandles(List<CandleDto> candleDtos, long idCompany) {
        candleDao.saveCandles(candleDtos, idCompany);
    }

    @Override
    @Transactional
    public void saveCandle(AbstractCandle candle, long idCompany) {
        candleDao.saveCandle(candle, idCompany);
    }

    @Override
    @Transactional
    public void saveCandle(AbstractCandle candle, long idCompany, boolean isAnomaly) {
        candleDao.saveCandle(candle, idCompany, isAnomaly);
    }
}
