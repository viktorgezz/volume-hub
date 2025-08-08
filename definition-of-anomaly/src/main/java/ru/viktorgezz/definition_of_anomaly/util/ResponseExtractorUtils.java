package ru.viktorgezz.definition_of_anomaly.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public abstract class ResponseExtractorUtils {

    private static final String REQUEST_ERROR_STATUS = "Ошибка при запросе: статус {}";
    private static final String REQUEST_EXECUTION_ERROR = "Ошибка при выполнении запроса: {}";
    private static final Logger log = LoggerFactory.getLogger(ResponseExtractorUtils.class);

    public static <T> T extractResponseBodyOrThrow(ResponseEntity<T> response) {
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
