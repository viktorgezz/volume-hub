package ru.viktorgezz.definition_of_anomaly.candle.intf;

import ru.viktorgezz.definition_of_anomaly.candle.model.CandleDto;

import java.util.List;
import java.util.Map;

public interface CandleDataClient { // переименовать класс

    Map<String, List<CandleDto>> fetchMinuteCandlesForLastDay();

    List<CandleDto> fetchMinuteCandlesForLastHour(String figi);

    List<CandleDto> fetchMinuteCandlesForLastMinute(String figi);

    List<CandleDto> fetchLastTwoDaysCandle(String figi);

}
