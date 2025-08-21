package ru.viktorgezz.definition_of_anomaly.metric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.company.CompanyService;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.MetricDynamicService;

@Component
public class MetricScheduledLowering {

    private final MetricDynamicService metricDynamicService;
    private final CompanyService companyService;

    @Autowired
    public MetricScheduledLowering(
            MetricDynamicService metricDynamicService,
            CompanyService companyService
    ) {
        this.metricDynamicService = metricDynamicService;
        this.companyService = companyService;
    }

    @Scheduled(cron = "30 0 */2 * * 1-5", zone = "Europe/Moscow")
    public void loweringCoefficient() {
        companyService.getIdsCompany()
                .forEach(metricDynamicService::decreaseCoefficient);
    }
}
