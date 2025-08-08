package ru.viktorgezz.definition_of_anomaly.candle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleProcessingService;

@RestController
@RequestMapping("/anomaly/public/api/v1")
public class CandleMetricsController {

    private final CandleProcessingService candleProcessing;

    @Autowired
    public CandleMetricsController(CandleProcessingService candleProcessing) {
        this.candleProcessing = candleProcessing;
    }

    @PostMapping("/upload-candles")
    public String uploadMinuteCandles() {
        candleProcessing.uploadCandlesForLastDay();
        return "Успех";
    }

    @PostMapping("/metrics")
    public String updateMetrics() {
        candleProcessing.calculateStatsMetrics();
        return "Успех";
    }

    @PostMapping("/metrics-irvin")
    public String updateMetricsByIrvin() {
        candleProcessing.calculateStatsMetricsByIrvin();
        return "Успех";
    }
}
