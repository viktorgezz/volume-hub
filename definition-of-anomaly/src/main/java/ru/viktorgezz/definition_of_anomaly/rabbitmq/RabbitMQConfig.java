package ru.viktorgezz.definition_of_anomaly.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.template.anomaly.queue}")
    private String anomalyQueueName;

    @Value("${spring.rabbitmq.template.company.queue}")
    private String updateQueue;

    @Value("${spring.rabbitmq.template.queue}")
    private String queueMinute;

    @Value("${spring.rabbitmq.template.anomaly.exchange}")
    private String anomalyExchangeName;

    @Value("${spring.rabbitmq.template.anomaly.routing-key}")
    private String anomalyRoutingKey;

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean("queueMinute")
    public Queue queueMinute() {
        return new Queue(queueMinute, false);
    }

    @Bean("queueUpdate")
    public Queue queueUpdate() {
        return new Queue(updateQueue, true);
    }

    @Bean("anomalyQueue")
    public Queue queueAnomaly() {
        return new Queue(anomalyQueueName, false);
    }

    @Bean
    public TopicExchange exchangeAnomaly() {
        return new TopicExchange(anomalyExchangeName);
    }

    @Bean
    public Binding bindingAnomaly() {
        return BindingBuilder.bind(queueAnomaly()).to(exchangeAnomaly()).with(anomalyRoutingKey);
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
