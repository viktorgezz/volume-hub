package ru.viktorgezz.definition_of_anomaly.metric.service.intrf;

import ru.viktorgezz.definition_of_anomaly.metric.model.MetricDynamic;

import java.util.Optional;

public interface MetricDynamicService {

    void decreaseCoefficient(Long idCompany);

    void increaseCoefficient(Long idCompany);

    Optional<MetricDynamic> findCoefficientByIdCompany(Long idCompany);

    void save(MetricDynamic metricDynamic);

    void loadDataMetricDynamic();

}
