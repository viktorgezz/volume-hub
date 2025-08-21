package ru.viktorgezz.definition_of_anomaly.metric.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleStatisticsService;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.IrvinStatisticsCalculator;
import ru.viktorgezz.definition_of_anomaly.company.CompanyService;
import ru.viktorgezz.definition_of_anomaly.metric.model.Metric;
import ru.viktorgezz.definition_of_anomaly.metric.model.MetricByIrvin;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.MetricService;

@Service
public class IrvinStatisticsCalculatorImpl implements IrvinStatisticsCalculator {

    private static final String COMPANY_STATS_BY_IRVIN_SAVED = "Company: {} is add standard deviation: {}, average: {}, critical value: {} in table";
    private static final Logger log = LoggerFactory.getLogger(IrvinStatisticsCalculatorImpl.class);

    private final CompanyService companyService;
    private final CandleStatisticsService candleStatisticsService;
    private final MetricService metricService;

    @Autowired
    public IrvinStatisticsCalculatorImpl(
            CompanyService companyService,
            CandleStatisticsService candleStatisticsService,
            MetricService metricService
    ) {
        this.companyService = companyService;
        this.candleStatisticsService = candleStatisticsService;
        this.metricService = metricService;
    }

    @Transactional
    @Override
    public void calculateStatsMetricsByIrvin() {
        companyService
                .getIdsCompany()
                .forEach(id -> {
                            Metric tempMetric = candleStatisticsService.computeStandardDeviationAndAverage(id);
                            MetricByIrvin newMetric = new MetricByIrvin(
                                    tempMetric.getStandardDeviation(),
                                    tempMetric.getAverage(),
                                    candleStatisticsService.computeCriticalValue(id)
                            );

                            metricService.save(id, newMetric);
                            log.info(COMPANY_STATS_BY_IRVIN_SAVED, id, newMetric.getStandardDeviation(), newMetric.getAverage(), newMetric.getCriticalValue());
                        }
                );
    }
}
