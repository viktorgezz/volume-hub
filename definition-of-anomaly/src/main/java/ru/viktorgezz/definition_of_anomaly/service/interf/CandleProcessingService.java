package ru.viktorgezz.definition_of_anomaly.service.interf;

public interface CandleProcessingService {

    void uploadCandlesForLastDay();

    void calculateStatsMetrics();
}
