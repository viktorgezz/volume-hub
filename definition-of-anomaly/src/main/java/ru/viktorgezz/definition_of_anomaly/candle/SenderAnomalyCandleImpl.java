package ru.viktorgezz.definition_of_anomaly.candle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.candle.intf.SenderAnomalyCandle;
import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleAnomalyDto;
import ru.viktorgezz.definition_of_anomaly.candle.model.CandleMessage;
import ru.viktorgezz.definition_of_anomaly.rabbitmq.CandleAnomalyProducer;


@Component
public class SenderAnomalyCandleImpl implements SenderAnomalyCandle {

    private static final Logger log = LoggerFactory.getLogger(SenderAnomalyCandleImpl.class);

    private static final String STUB_FOR_EXCEPTION = "Заглушка для исключения {}";
    private static final String ANOMALOUS_VOLUME = "Отправленный аномальный объём: {} {}";

    private final ConverterCandle converter;
    private final CandleAnomalyProducer producer;

    @Autowired
    public SenderAnomalyCandleImpl(
            ConverterCandle converter,
            CandleAnomalyProducer producer
    ) {
        this.converter = converter;
        this.producer = producer;
    }

    public void send(CandleMessage candleMessage) {
        try {
            CandleAnomalyDto anomaly = converter.convertToCandleAnomalyDto(candleMessage);

            producer.sendAnomalyCandle(anomaly);
            log.info(ANOMALOUS_VOLUME, anomaly.getName(), anomaly.getVolume());
        } catch (Exception e) {
            log.error(STUB_FOR_EXCEPTION, e.getMessage());
        }
    }
}
