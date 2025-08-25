package ru.viktorgezz.definition_of_anomaly.candle.intf;

import ru.viktorgezz.definition_of_anomaly.candle.model.CandleMessage;

public interface SenderAnomalyCandle {

    void send(CandleMessage candle);
}
