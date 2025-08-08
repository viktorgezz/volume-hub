package ru.viktorgezz.api_T_connector.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.viktorgezz.api_T_connector.candle.CandleMessage;

@Service
public class RabbitMQProducer {

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchangeName;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCandle(CandleMessage candle) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, candle);
    }
}
