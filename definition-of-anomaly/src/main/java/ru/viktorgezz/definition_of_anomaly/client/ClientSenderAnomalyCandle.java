package ru.viktorgezz.definition_of_anomaly.client;

import ru.viktorgezz.definition_of_anomaly.dto.CandleMessage;

public interface ClientSenderAnomalyCandle {

    void send(CandleMessage candle);
}
