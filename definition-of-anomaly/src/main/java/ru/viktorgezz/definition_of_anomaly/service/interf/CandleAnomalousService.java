package ru.viktorgezz.definition_of_anomaly.service.interf;

import ru.viktorgezz.definition_of_anomaly.dto.CandleMessage;

public interface CandleAnomalousService {

    void foundAnomalyCandle(CandleMessage candle);
}
