package ru.viktorgezz.definition_of_anomaly.candle.service.intf;

import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleMessageDto;

public interface CandleAnomalousService {

    void foundAnomalyCandle(CandleMessageDto candle);
}
