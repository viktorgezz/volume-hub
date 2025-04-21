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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ClientTInvest implements ClientInvest {

    private static final Logger log = LoggerFactory.getLogger(ClientTInvest.class);

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
        try {
            ResponseEntity<Map<String, List<CandleDto>>> response = rT.exchange(
                    String.format("%s/candle/minute/for-last-day", API_SERVICE_VOLUME),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, List<CandleDto>>>() {
                    }
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error("Ошибка при запросе: статус {}", response.getStatusCode());
                return Collections.emptyMap();
            }
        } catch (Exception e) {
            log.error("Ошибка при выполнении запроса: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    public String fetchNameCompanyByFigi(String figi) {
        return rT.getForObject(
                String.format("%s/company-name/%s", API_SERVICE_VOLUME, figi),
                String.class
        );
    }

    public CandleDto fetchDayCandleCurrDay(String figi) {
        try {
            ResponseEntity<CandleDto> response =  rT.exchange(
                    String.format("%s/candle/day/%s", API_SERVICE_VOLUME, figi),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<CandleDto>() {
                    }
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error("Ошибка при запросе: статус {}", response.getStatusCode());
                throw new RuntimeException();
            }
        } catch (RestClientException e) {
            log.error("Ошибка при выполнении запроса: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
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
            log.error("Ошибка при принятие объектов акций");
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
            log.error("Ошибка при принятие объектов figi");
            return List.of();
        }
    }
}
