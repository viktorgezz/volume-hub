package ru.viktorgezz.definition_of_anomaly.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.company.Company;
import ru.viktorgezz.definition_of_anomaly.company.service.CompanyUpdateService;

@Component
public class CompanyUpdateConsumer {

    private final CompanyUpdateService companyUpdateService;

    @Autowired
    public CompanyUpdateConsumer(CompanyUpdateService companyUpdateService) {
        this.companyUpdateService = companyUpdateService;
    }

    @RabbitListener(queues = "${spring.rabbitmq.template.company.queue}")
    public void receiveUpdateCompany(CompanyMessage companyMessage) {
        companyUpdateService.update(
                new Company(
                        companyMessage.getName(),
                        companyMessage.getTickerUpdate(),
                        companyMessage.getFigi()
                ),
                companyMessage.getLookupTicker());
    }
}

