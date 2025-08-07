package ru.viktorgezz.api_T_connector.service.impl;

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
import ru.viktorgezz.api_T_connector.model.Share;
import ru.viktorgezz.api_T_connector.service.interf.ShareService;

import java.util.List;

@Component
public class ShareServiceImpl implements ShareService {

    private static final String REQUEST_ERROR_STATUS = "Ошибка при запросе: статус {}";
    private static final String REQUEST_EXECUTION_ERROR = "Ошибка при выполнении запроса: {}";

    private final String urlGuide;

    private static final Logger log = LoggerFactory.getLogger(ShareServiceImpl.class);

    private final RestTemplate restTemplate;

    @Autowired
    public ShareServiceImpl(
            @Value("${url.guide}") String urlGuide,
            RestTemplate restTemplate
    ) {
        this.urlGuide = urlGuide;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<String> getAllFigis() {
        return extractResponseBodyOrThrow(
                restTemplate.exchange(
                        String.format("%s/company/figi", urlGuide),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<String>>() {
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
