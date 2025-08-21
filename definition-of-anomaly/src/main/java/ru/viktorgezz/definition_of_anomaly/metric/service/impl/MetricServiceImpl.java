package ru.viktorgezz.definition_of_anomaly.metric.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.definition_of_anomaly.metric.model.MetricByIrvin;
import ru.viktorgezz.definition_of_anomaly.metric.MetricDao;
import ru.viktorgezz.definition_of_anomaly.metric.StandardDeviationAndCriticalValue;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.MetricService;

import java.math.BigDecimal;

@Service
public class MetricServiceImpl implements MetricService {

    private final MetricDao metricDao;

    @Autowired
    public MetricServiceImpl(
            MetricDao metricDao
    ) {
        this.metricDao = metricDao;
    }

    @Override
    @Transactional
    public void save(
            Long idCompany,
            MetricByIrvin statsMetric
    ) {
        metricDao.save(idCompany, statsMetric);
    }

    @Override
    public BigDecimal computeAbsoluteDifference(
            Long idCompany,
            Long volume
    ) {
        return metricDao.computeAbsoluteDifference(idCompany, volume);
    }


    @Override
    public StandardDeviationAndCriticalValue getStandardDeviationAndCriticalValue(long idCompany) {
        return new StandardDeviationAndCriticalValue(
                metricDao.getStandardDeviation(idCompany),
                metricDao.getCriticalValue(idCompany)
        );
    }
}
