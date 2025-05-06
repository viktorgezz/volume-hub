package ru.viktorgezz.definition_of_anomaly.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.viktorgezz.definition_of_anomaly.dto.CandleAnomalyDto;
import ru.viktorgezz.definition_of_anomaly.dto.CandleMessage;
import ru.viktorgezz.definition_of_anomaly.util.ConverterCandleMessageToAnomalyDto;


@Component
public class ClientSenderAnomalyCandleImpl implements ClientSenderAnomalyCandle {

    private static final Logger log = LoggerFactory.getLogger(ClientSenderAnomalyCandleImpl.class);

    private static final String STUB_FOR_EXCEPTION = "Заглушка для исключения {}";
    private static final String ANOMALOUS_VOLUME_SENT = "Отправлен аномальный объём: {} {}";
    private static final String ANOMALOUS_VOLUME = "Аномальный объём: {}";

    private final String URI_TELEGRAM;

    private final ConverterCandleMessageToAnomalyDto converter;

    private final RestTemplate rT;

    @Autowired
    public ClientSenderAnomalyCandleImpl(
            @Value("${service.telegram.uri}") String uriTelegram,
            ConverterCandleMessageToAnomalyDto converter,
            RestTemplate rT
    ) {
        URI_TELEGRAM = uriTelegram;
        this.converter = converter;
        this.rT = rT;
    }

    public void send(CandleMessage candleMessage) {
        CandleAnomalyDto anomaly = converter.convertToCandleAnomalyDto(candleMessage);
        try {
            String message = rT.postForObject(URI_TELEGRAM, anomaly, String.class);
            log.info(ANOMALOUS_VOLUME_SENT, anomaly, message);
        } catch (Exception e) {
            log.error(STUB_FOR_EXCEPTION, e.getMessage());
        }
        log.info(ANOMALOUS_VOLUME, anomaly);

    }
}
