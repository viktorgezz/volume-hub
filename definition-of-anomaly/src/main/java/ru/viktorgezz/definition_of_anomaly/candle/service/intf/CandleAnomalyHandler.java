package ru.viktorgezz.definition_of_anomaly.candle.service.intf;

import ru.viktorgezz.definition_of_anomaly.candle.model.CandleMessage;

public interface CandleAnomalyHandler {

    void foundAnomalyCandle(CandleMessage candle);
}
