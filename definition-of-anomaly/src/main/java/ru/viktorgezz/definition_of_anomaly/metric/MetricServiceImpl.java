package ru.viktorgezz.definition_of_anomaly.metric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            Metric statsMetric
    ) {
        metricDao.save(idCompany, statsMetric);
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
    public BigDecimal computeZScore(
            long volume,
            long idCompany
    ) {
        return metricDao.computeZScore(volume, idCompany);
    }

    @Override
    public BigDecimal computeAbsoluteDifference(
            Long idCompany,
            Long volume
    ) {
        return metricDao.computeAbsoluteDifference(idCompany, volume);
    }

    @Override
    public BigDecimal getZScoreFromTable(long idCompany) {
        return metricDao.getZScoreFromTable(idCompany);
    }

    @Override
    public StandardDeviationAndCriticalValue getStandardDeviationAndCriticalValue(long idCompany) {
        return new StandardDeviationAndCriticalValue(
                metricDao.getStandardDeviation(idCompany),
                metricDao.getCriticalValue(idCompany)
        );
    }
}
