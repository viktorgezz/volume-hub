package ru.viktorgezz.definition_of_anomaly.candle.intf;

import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleDto;

import java.util.List;
import java.util.Map;

public interface CandleApiClient {

    Map<String, List<CandleDto>> fetchMinuteCandlesForLastDay();

    List<CandleDto> fetchMinuteCandlesForLastHour(String figi);

    List<CandleDto> fetchLastTwoDaysCandle(String figi);

}
