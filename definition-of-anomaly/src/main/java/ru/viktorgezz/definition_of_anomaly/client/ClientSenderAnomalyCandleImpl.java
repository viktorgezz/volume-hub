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
            rT.postForEntity(URI_TELEGRAM, anomaly, CandleAnomalyDto.class);
        } catch (Exception e) {
            log.error("Заглушка для исключения");
        }
        log.info("Отправлен аномальный объём: {}", anomaly);
    }
}
