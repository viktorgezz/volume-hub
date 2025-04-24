package ru.viktorgezz.api_T_connector.service.interf;

public interface CandleStreamService {

    void streamLatestMinuteCandles();

    void cancelStream();
}
