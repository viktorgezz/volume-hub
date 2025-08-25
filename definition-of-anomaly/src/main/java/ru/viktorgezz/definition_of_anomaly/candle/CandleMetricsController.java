package ru.viktorgezz.definition_of_anomaly.candle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleProcessingService;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.IrvinStatisticsCalculator;

@RestController
@RequestMapping("/anomaly/public/api/v1")
public class CandleMetricsController {

    private final CandleProcessingService candleProcessing;
    private final IrvinStatisticsCalculator irvinStatisticsCalculator;

    @Autowired
    public CandleMetricsController(
            CandleProcessingService candleProcessing,
            IrvinStatisticsCalculator irvinStatisticsCalculator
    ) {
        this.candleProcessing = candleProcessing;
        this.irvinStatisticsCalculator = irvinStatisticsCalculator;
    }

    @PostMapping("/upload-candles")
    public String uploadMinuteCandles() {
        candleProcessing.uploadCandlesForLastDay();
        return "Успех";
    }

    @PostMapping("/metrics-irvin")
    public String updateMetricsByIrvin() {
        irvinStatisticsCalculator.calculateStatsMetricsByIrvin();
        return "Успех";
    }
}
