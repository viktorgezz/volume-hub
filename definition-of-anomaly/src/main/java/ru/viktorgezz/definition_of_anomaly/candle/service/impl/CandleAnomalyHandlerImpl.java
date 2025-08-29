package ru.viktorgezz.definition_of_anomaly.candle.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.viktorgezz.definition_of_anomaly.candle.intf.SenderAnomalyCandle;
import ru.viktorgezz.definition_of_anomaly.candle.model.CandleMessage;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleAnomalyHandler;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.AnomalyDetectionService;
import ru.viktorgezz.definition_of_anomaly.company.service.CompanyService;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.MetricDynamicService;

@Service
public class CandleAnomalyHandlerImpl implements CandleAnomalyHandler {

    private static final Logger log = LoggerFactory.getLogger(CandleAnomalyHandlerImpl.class);

    private static final String ANOMALOUS_VOLUME_DETECTED = "Найден аномальный объем figi: {}, volume: {}, time: {}";

    private final CompanyService companyService;
    private final MetricDynamicService metricDynamicService;
    private final AnomalyDetectionService anomalyDetectionService;
    private final SenderAnomalyCandle sender;

    @Autowired
    public CandleAnomalyHandlerImpl(
            CompanyService companyService,
            MetricDynamicService metricDynamicService,
            AnomalyDetectionService anomalyDetectionService,
            SenderAnomalyCandle sender
    ) {
        this.companyService = companyService;
        this.metricDynamicService = metricDynamicService;
        this.anomalyDetectionService = anomalyDetectionService;
        this.sender = sender;
    }

    @Override
    public void foundAnomalyCandle(CandleMessage candle) {
        final long idCompany = companyService.getIdCompanyByFigi(candle.getFigi());

        if (anomalyDetectionService.isAnomaly(candle, idCompany)) {
            log.info(ANOMALOUS_VOLUME_DETECTED, candle.getFigi(), candle.getVolume(), candle.getTime());
            sender.send(candle);
            metricDynamicService.increaseCoefficient(companyService.getIdCompanyByFigi(candle.getFigi()));
        }
    }

}
