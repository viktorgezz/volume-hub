package ru.viktorgezz.api_T_connector.candle.service.interf;

public interface CandleStreamService {

    void streamLatestMinuteCandles();

    void cancelStream();
}
