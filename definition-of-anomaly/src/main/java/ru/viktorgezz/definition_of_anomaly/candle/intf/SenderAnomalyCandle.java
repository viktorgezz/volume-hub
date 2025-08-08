package ru.viktorgezz.definition_of_anomaly.candle.intf;

import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleMessageDto;

public interface SenderAnomalyCandle {

    void send(CandleMessageDto candle);
}
