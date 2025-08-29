package ru.viktorgezz.company_info.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProducerUpdMess {

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchangeUpdMess;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKeyUpdMess;

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(CompanyMessage message) {
        rabbitTemplate.convertAndSend(exchangeUpdMess, routingKeyUpdMess, message);
    }
}
