package ru.viktorgezz.definition_of_anomaly.client;

import ru.viktorgezz.definition_of_anomaly.dto.CandleDto;
import ru.viktorgezz.definition_of_anomaly.dto.CompanyRsDto;

import java.util.List;
import java.util.Map;

public interface ClientRecipientInvest { // переименовать класс

    Map<String, List<CandleDto>> fetchMinuteCandlesForLastDay();

    CompanyRsDto fetchCompanyByFigi(String figi);

    List<CandleDto> fetchMinuteCandlesForLastHour(String figi);

    List<CandleDto> fetchMinuteCandlesForLastMinute(String figi);

    List<CandleDto> fetchLastTwoDaysCandle(String figi);

}
