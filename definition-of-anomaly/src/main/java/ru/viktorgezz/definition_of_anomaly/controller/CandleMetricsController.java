package ru.viktorgezz.definition_of_anomaly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.definition_of_anomaly.service.interf.CandleProcessingService;

@RestController
public class CandleMetricsController {

    private final CandleProcessingService candleProcessing;

    @Autowired
    public CandleMetricsController(CandleProcessingService candleProcessing) {
        this.candleProcessing = candleProcessing;
    }

    @GetMapping("/upload-candles")
    public String uploadMinuteCandles() {
        candleProcessing.uploadCandlesForLastDay();
        return "Успех";
    }

    @GetMapping("/metrics")
    public String updateMetrics() {
        candleProcessing.calculateStatsMetrics();
        return "Успех";
    }
}
