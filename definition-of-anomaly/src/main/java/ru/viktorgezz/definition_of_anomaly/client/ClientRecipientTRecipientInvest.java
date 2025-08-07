package ru.viktorgezz.definition_of_anomaly.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.viktorgezz.definition_of_anomaly.dto.CandleDto;
import ru.viktorgezz.definition_of_anomaly.dto.CompanyRsDto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ClientRecipientTRecipientInvest implements ClientRecipientInvest {

    private static final Logger log = LoggerFactory.getLogger(ClientRecipientTRecipientInvest.class);

    private static final String REQUEST_ERROR_STATUS = "Ошибка при запросе: статус {}";
    private static final String REQUEST_EXECUTION_ERROR = "Ошибка при выполнении запроса: {}";

    private final String API_SERVICE_URL;
    private final String GUIDE_SERVICE_URL;

    private final RestTemplate rT;

    @Autowired
    public ClientRecipientTRecipientInvest(
            RestTemplate rT,
            @Value("${service.api.url}") String apiServiceVolume,
            @Value("${service.guide.url}") String guideServiceUrl
    ) {
        this.rT = rT;
        API_SERVICE_URL = apiServiceVolume;
        GUIDE_SERVICE_URL = guideServiceUrl;
    }

    public Map<String, List<CandleDto>> fetchMinuteCandlesForLastDay() {
        return extractResponseBodyOrThrow(rT
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
        return extractResponseBodyOrThrow(rT
                .exchange(
                        String.format("%s/api/v1/candle/minute/for-last-hour/%s", API_SERVICE_URL, figi),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<CandleDto>>() {
                        }
                )
        );
    }

    public List<CandleDto> fetchMinuteCandlesForLastMinute(String figi) {
        return extractResponseBodyOrThrow(rT
                .exchange(
                        String.format("%s/api/v1/candle/last-two-minute/%s", API_SERVICE_URL, figi),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<CandleDto>>() {
                        }
                )
        );
    }


    public List<CandleDto> fetchLastTwoDaysCandle(String figi) {
        return extractResponseBodyOrThrow(rT
                .exchange(
                        String.format("%s/api/v1/candle/last-two-day/%s", API_SERVICE_URL, figi),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<CandleDto>>() {
                        }
                )
        );
    }

    public CompanyRsDto fetchCompanyByFigi(String figi) {
        return extractResponseBodyOrThrow(rT
                .exchange(
                        String.format("%s/company/%s", GUIDE_SERVICE_URL, figi),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<CompanyRsDto>() {
                        }
                )
        );
    }

    private <T> T extractResponseBodyOrThrow(ResponseEntity<T> response) {
        try {
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error(REQUEST_ERROR_STATUS, response.getStatusCode());
                throw new RuntimeException();
            }
        } catch (RestClientException e) {
            log.error(REQUEST_EXECUTION_ERROR, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
