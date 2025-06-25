package ru.viktorgezz.definition_of_anomaly.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.dto.CandleMessage;
import ru.viktorgezz.definition_of_anomaly.service.interf.CandleAnomalousService;

@Component
public class CandleMinuteConsumer {

    private static final Logger log = LoggerFactory.getLogger(CandleMinuteConsumer.class);

    private static final String FIGI_AND_VOLUME_INFO = "figi: {}, volume: {}";

    private final CandleAnomalousService anomalousCandle;

    @Autowired
    public CandleMinuteConsumer(
            @Qualifier("candleAnomalousByIrvinServiceImpl") CandleAnomalousService anomalousCandle
    ) {
        this.anomalousCandle = anomalousCandle;
    }

    @RabbitListener(queues = "${spring.rabbitmq.template.queue}")
    public void receiveMinuteCandles(CandleMessage candleMessage) {
        log.info(FIGI_AND_VOLUME_INFO, candleMessage.getFigi(), candleMessage.getVolume());
        anomalousCandle.foundAnomalyCandle(candleMessage);
    }
}
