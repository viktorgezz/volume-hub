package ru.viktorgezz.api_T_connector.service.interf;

import ru.viktorgezz.api_T_connector.model.CustomCandle;

import java.util.List;
import java.util.Map;

public interface HistoricalCandleMarketService {

    List<CustomCandle> getLastTwoDaysCandle(String figi);

    List<CustomCandle> getLastTwoMinuteCandle(String figi);

    List<CustomCandle> getMinuteCandlesForLastHourByFigi(final String figi);

    Map<String, List<CustomCandle>> getMinuteCandlesForLastDayAllFigis();

}
