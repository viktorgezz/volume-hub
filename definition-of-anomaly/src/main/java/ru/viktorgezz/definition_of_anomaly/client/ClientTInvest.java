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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ClientTInvest implements ClientInvest {

    private static final Logger log = LoggerFactory.getLogger(ClientTInvest.class);

    private static final String REQUEST_ERROR_STATUS = "Ошибка при запросе: статус {}";
    private static final String REQUEST_EXECUTION_ERROR = "Ошибка при выполнении запроса: {}";
    private static final String ERROR_RECEIVING_STOCK_OBJECTS = "Ошибка при принятие объектов акций";
    private static final String ERROR_RECEIVING_FIGI = "Ошибка при принятие всех figi";

    private final String API_SERVICE_VOLUME;

    private final RestTemplate rT;

    @Autowired
    public ClientTInvest(
            RestTemplate rT,
            @Value("${service.volume.url}") String apiServiceVolume) {
        this.rT = rT;
        API_SERVICE_VOLUME = apiServiceVolume;
    }

    public Map<String, List<CandleDto>> fetchMinuteCandlesForLastDay() {
        return extractResponseBodyOrThrow(rT
                .exchange(
                        String.format("%s/candle/minute/for-last-day", API_SERVICE_VOLUME),
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
                        String.format("%s/candle/minute/for-last-hour/%s", API_SERVICE_VOLUME, figi),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<CandleDto>>() {
                        }
                )
        );
    }



    public CandleDto fetchDayCandleCurr(String figi) {
        return extractResponseBodyOrThrow(rT
                .exchange(
                        String.format("%s/candle/day/%s", API_SERVICE_VOLUME, figi),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<CandleDto>() {
                        }
                )
        );
    }

    public String fetchNameCompanyByFigi(String figi) {
        return rT.getForObject(
                String.format("%s/company-name/%s", API_SERVICE_VOLUME, figi),
                String.class
        );
    }

    public String fetchTickerCompanyByFigi(String figi) {
        return rT.getForObject(
                String.format("%s/company-ticker/%s", API_SERVICE_VOLUME, figi),
                String.class
        );
    }

    public List<String> fetchShares() {
        try {
            return Arrays.asList(
                    Objects.requireNonNull(
                            rT.getForObject(
                                    String.format("%s/share", API_SERVICE_VOLUME),
                                    String[].class
                            )
                    )
            );
        } catch (NullPointerException e) {
            log.error(ERROR_RECEIVING_STOCK_OBJECTS);
            return List.of();
        }
    }

    public List<String> fetchFigis() {
        try {
            return Arrays.asList(
                    Objects.requireNonNull(
                            rT.getForObject(
                                    String.format("%s/figi", API_SERVICE_VOLUME),
                                    String[].class
                            )
                    )
            );
        } catch (NullPointerException e) {
            log.error(ERROR_RECEIVING_FIGI);
            return List.of();
        }
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
