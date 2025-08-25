package ru.viktorgezz.definition_of_anomaly.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleAnomalyDto;

@Component
public class CandleAnomalyProducer {

    @Value("${spring.rabbitmq.template.anomaly.exchange}")
    private String anomalyExchangeName;

    @Value("${spring.rabbitmq.template.anomaly.routing-key}")
    private String anomalyRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CandleAnomalyProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAnomalyCandle(CandleAnomalyDto candle) {
        rabbitTemplate.convertAndSend(anomalyExchangeName, anomalyRoutingKey, candle);
    }


}
