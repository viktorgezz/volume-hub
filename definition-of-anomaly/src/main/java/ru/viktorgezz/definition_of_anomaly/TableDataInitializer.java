package ru.viktorgezz.definition_of_anomaly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleProcessingService;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.IrvinStatisticsCalculator;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.MetricDynamicService;

@Component
@ConditionalOnProperty(
        value = "custom.table-data-initializer",
        havingValue = "true",
        matchIfMissing = true
)
public class TableDataInitializer implements CommandLineRunner {

    private final CandleProcessingService candleProcessingService;
    private final IrvinStatisticsCalculator irvinStatisticsCalculator;
    private final MetricDynamicService metricDynamicService;

    @Autowired
    public TableDataInitializer(
            CandleProcessingService candleProcessingService,
            IrvinStatisticsCalculator irvinStatisticsCalculator,
            MetricDynamicService metricDynamicService
    ) {
        this.candleProcessingService = candleProcessingService;
        this.irvinStatisticsCalculator = irvinStatisticsCalculator;
        this.metricDynamicService = metricDynamicService;
    }

    @Override
    public void run(String... args) throws Exception {
        candleProcessingService.uploadCandlesForLastDay();
        irvinStatisticsCalculator.calculateStatsMetricsByIrvin();
        metricDynamicService.loadDataMetricDynamic();
    }
}
