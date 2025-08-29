package ru.viktorgezz.definition_of_anomaly.candle.service.intf;

import ru.viktorgezz.definition_of_anomaly.candle.model.AbstractCandle;
import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleDto;

import java.util.List;

public interface CandleService {

    void saveCandles(List<CandleDto> candleDtos, long idCompany);

    void saveCandle(AbstractCandle candle, long idCompany);

    void saveCandle(AbstractCandle candle, long idCompany, boolean isAnomaly);
}
