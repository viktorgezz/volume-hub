package ru.viktorgezz.definition_of_anomaly.client;

import ru.viktorgezz.definition_of_anomaly.dto.CandleDto;

import java.util.List;
import java.util.Map;

public interface ClientInvest {

    Map<String, List<CandleDto>> fetchMinuteCandlesForLastDay();

    String fetchNameCompanyByFigi(String figi);

    CandleDto fetchDayCandleCurrDay(String figi);
}
