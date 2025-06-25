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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ClientRecipientTRecipientInvest implements ClientRecipientInvest {

    private static final Logger log = LoggerFactory.getLogger(ClientRecipientTRecipientInvest.class);

    private static final String REQUEST_ERROR_STATUS = "Ошибка при запросе: статус {}";
    private static final String REQUEST_EXECUTION_ERROR = "Ошибка при выполнении запроса: {}";
    private static final String ERROR_RECEIVING_STOCK_OBJECTS = "Ошибка при принятие объектов акций";
    private static final String ERROR_RECEIVING_FIGI = "Ошибка при принятие всех figi";

    private final String API_SERVICE_VOLUME;

    private final RestTemplate rT;

    @Autowired
    public ClientRecipientTRecipientInvest(
            RestTemplate rT,
            @Value("${service.volume.url}") String apiServiceVolume) {
        this.rT = rT;
        API_SERVICE_VOLUME = apiServiceVolume;
    }

    public Map<String, List<CandleDto>> fetchMinuteCandlesForLastDay() {
        return extractResponseBodyOrThrow(rT
                .exchange(
                        String.format("%s/api/v1/candle/minute/for-last-day", API_SERVICE_VOLUME),
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
                        String.format("%s/api/v1/candle/minute/for-last-hour/%s", API_SERVICE_VOLUME, figi),
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
                        String.format("%s/api/v1/candle/last-two-minute/%s", API_SERVICE_VOLUME, figi),
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
                        String.format("%s/api/v1/candle/last-two-day/%s", API_SERVICE_VOLUME, figi),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<CandleDto>>() {
                        }
                )
        );
    }

    @Override
    public void saveMinuteCandleHistoryToFile(String figi) {
        try {
            int currentYear = LocalDateTime.now().getYear();

            byte[] zipData = rT.getForObject(
                String.format("%s/api/historical/download?figi=%s&year=%d", 
                    API_SERVICE_VOLUME, figi, currentYear),
                byte[].class
            );
            


        } catch (Exception e) {
            log.error("Error while saving historical data for FIGI: {}", figi, e);
            throw new RuntimeException("Failed to save historical data", e);
        }
    }

    public String fetchNameCompanyByFigi(String figi) {
        return rT.getForObject(
                String.format("%s/api/v1/company-name/%s", API_SERVICE_VOLUME, figi),
                String.class
        );
    }

    public String fetchTickerCompanyByFigi(String figi) {
        return rT.getForObject(
                String.format("%s/api/v1/company-ticker/%s", API_SERVICE_VOLUME, figi),
                String.class
        );
    }

    public List<String> fetchShares() {
        try {
            return Arrays.asList(
                    Objects.requireNonNull(
                            rT.getForObject(
                                    String.format("%s/api/v1/share", API_SERVICE_VOLUME),
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
                                    String.format("%s/api/v1/figi", API_SERVICE_VOLUME),
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
