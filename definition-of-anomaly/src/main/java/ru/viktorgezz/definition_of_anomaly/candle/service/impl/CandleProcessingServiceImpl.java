package ru.viktorgezz.definition_of_anomaly.candle.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.definition_of_anomaly.candle.intf.CandleDataClient;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleStatisticsService;
import ru.viktorgezz.definition_of_anomaly.company.CompanyClient;
import ru.viktorgezz.definition_of_anomaly.candle.dto.CandleDto;
import ru.viktorgezz.definition_of_anomaly.company.CompanyRsDto;
import ru.viktorgezz.definition_of_anomaly.company.CompanyService;
import ru.viktorgezz.definition_of_anomaly.metric.MetricByIrvin;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleProcessingService;
import ru.viktorgezz.definition_of_anomaly.metric.Metric;
import ru.viktorgezz.definition_of_anomaly.metric.MetricService;

import java.util.List;
import java.util.Map;

@Service
public class CandleProcessingServiceImpl implements CandleProcessingService {

    private static final Logger log = LoggerFactory.getLogger(CandleProcessingServiceImpl.class);

    private static final String COMPANY_ADDED_WITH_FIGI_AND_NAME = "figi: {} and name: {} add in Company, ticker: {}";
    private static final String CANDLE_LIST_SIZE_FOR_FIGI = "figi: {}, size list candles: {}";
    private static final String CANDLES_PROCESSED_FOR_FIGI_COUNT = "Свечи обработаны количество figi: {}";
    private static final String COMPANY_STATS_SAVED = "Company: {} is add standard deviation: {} and average: {} in table";
    private static final String COMPANY_STATS_BY_IRVIN_SAVED = "Company: {} is add standard deviation: {}, average: {}, critical value: {} in table";

    private final CandleDataClient candleDataClient;
    private final CompanyClient companyClient;
    private final CompanyService companyService;
    private final CandleStatisticsService candleStatisticsService;
    private final MetricService metricService;


    @Autowired
    public CandleProcessingServiceImpl(
            CandleDataClient candleDataClient,
            CompanyClient companyClient,
            CompanyService companyService, CandleStatisticsService candleStatisticsService,
            MetricService metricService
            ) {
        this.candleDataClient = candleDataClient;
        this.companyClient = companyClient;
        this.companyService = companyService;
        this.candleStatisticsService = candleStatisticsService;
        this.metricService = metricService;
    }

    @Transactional
    @Override
    public void uploadCandlesForLastDay() {
        Map<String, List<CandleDto>> figiAndCandles = candleDataClient.fetchMinuteCandlesForLastDay();
        figiAndCandles
                .keySet()
                .forEach(figi -> {
                            if (!companyService.isCompanyPresent(figi)) {
                                CompanyRsDto company = companyClient.fetchCompanyByFigi(figi);
                                companyService.save(figi, company.getName(), company.getTicker());
                                log.info(COMPANY_ADDED_WITH_FIGI_AND_NAME, figi, company.getName(), company.getTicker());
                            }

                            long idCompany = companyService.getIdCompanyByFigi(figi);
                            List<CandleDto> candleDtos = figiAndCandles.get(figi);

                            candleStatisticsService.saveCandles(candleDtos, idCompany);
                            log.info(CANDLE_LIST_SIZE_FOR_FIGI, figi, candleDtos.size());
                        }
                );
        log.info(CANDLES_PROCESSED_FOR_FIGI_COUNT, figiAndCandles.keySet().size());
    }

    @Transactional
    @Override
    public void calculateStatsMetrics() {
        companyService
                .getIdsCompany()
                .forEach(id -> {
                            Metric statsMetric = candleStatisticsService.computeStandardDeviationAndAverage(id);
                            metricService.save(id, statsMetric);

                            log.info(COMPANY_STATS_SAVED, id, statsMetric.getStandardDeviation(), statsMetric.getAverage());
                        }
                );
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
