package ru.viktorgezz.api_T_connector.service.interf;

import java.util.List;

public interface CandleStreamService {

    void streamLatestMinuteCandles(List<String> figis);

    void cancelStream();
}
