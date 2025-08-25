package ru.viktorgezz.definition_of_anomaly.candle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.viktorgezz.definition_of_anomaly.candle.intf.CandleApiClient;
import ru.viktorgezz.definition_of_anomaly.util.ResponseExtractorUtils;
import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleDto;

import java.util.List;
import java.util.Map;

@Component
public class CandleApiClientImpl implements CandleApiClient {

    private final String API_SERVICE_URL;

    private final RestTemplate rT;

    @Autowired
    public CandleApiClientImpl(
            RestTemplate rT,
            @Value("${service.api.url}") String apiServiceVolume
    ) {
        this.rT = rT;
        API_SERVICE_URL = apiServiceVolume;
    }

    public Map<String, List<CandleDto>> fetchMinuteCandlesForLastDay() {
        return ResponseExtractorUtils.extractResponseBodyOrThrow(rT
                .exchange(
                        String.format("%s/api/v1/candle/minute/for-last-day", API_SERVICE_URL),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, List<CandleDto>>>() {
                        }
                )
        );
    }

    public List<CandleDto> fetchMinuteCandlesForLastHour(String figi) {
        return ResponseExtractorUtils.extractResponseBodyOrThrow(rT
                .exchange(
                        String.format("%s/api/v1/candle/minute/for-last-hour/%s", API_SERVICE_URL, figi),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<CandleDto>>() {
                        }
                )
        );
    }


    public List<CandleDto> fetchLastTwoDaysCandle(String figi) {
        return ResponseExtractorUtils.extractResponseBodyOrThrow(rT
                .exchange(
                        String.format("%s/api/v1/candle/last-two-day/%s", API_SERVICE_URL, figi),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<CandleDto>>() {
                        }
                )
        );
    }
}
