package ru.viktorgezz.definition_of_anomaly.client;

import ru.viktorgezz.definition_of_anomaly.dto.CandleDto;

import java.util.List;
import java.util.Map;

public interface ClientRecipientInvest {

    Map<String, List<CandleDto>> fetchMinuteCandlesForLastDay();

    String fetchNameCompanyByFigi(String figi);

    List<CandleDto> fetchMinuteCandlesForLastHour(String figi);

    List<CandleDto> fetchMinuteCandlesForLastMinute(String figi);

    List<CandleDto> fetchLastTwoDaysCandle(String figi);

    void saveMinuteCandleHistoryToFile(String figi);
}
