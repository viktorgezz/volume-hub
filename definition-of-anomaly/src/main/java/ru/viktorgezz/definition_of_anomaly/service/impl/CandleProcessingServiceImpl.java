package ru.viktorgezz.definition_of_anomaly.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.definition_of_anomaly.client.ClientRecipientInvest;
import ru.viktorgezz.definition_of_anomaly.dao.CandleDao;
import ru.viktorgezz.definition_of_anomaly.dao.CompanyDao;
import ru.viktorgezz.definition_of_anomaly.dao.MetricDao;
import ru.viktorgezz.definition_of_anomaly.dto.CandleDto;
import ru.viktorgezz.definition_of_anomaly.dto.CompanyRsDto;
import ru.viktorgezz.definition_of_anomaly.model.MetricByIrvin;
import ru.viktorgezz.definition_of_anomaly.service.interf.CandleProcessingService;
import ru.viktorgezz.definition_of_anomaly.model.Metric;

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

    private final ClientRecipientInvest clientRecipientInvest;
    private final CompanyDao companyDao;
    private final CandleDao candleDao;
    private final MetricDao metricDao;


    @Autowired
    public CandleProcessingServiceImpl(
            ClientRecipientInvest clientRecipientInvest,
            CompanyDao companyDao,
            CandleDao candleDao,
            MetricDao metricDao) {
        this.clientRecipientInvest = clientRecipientInvest;
        this.companyDao = companyDao;
        this.candleDao = candleDao;
        this.metricDao = metricDao;
    }

    @Transactional
    @Override
    public void uploadCandlesForLastDay() {
        Map<String, List<CandleDto>> figiAndCandles = clientRecipientInvest.fetchMinuteCandlesForLastDay();
        figiAndCandles
                .keySet()
                .forEach(figi -> {
                            if (!companyDao.isCompanyPresent(figi)) {
                                CompanyRsDto company = clientRecipientInvest.fetchCompanyByFigi(figi);
                                companyDao.save(figi, company.getName(), company.getTicker());
                                log.info(COMPANY_ADDED_WITH_FIGI_AND_NAME, figi, company.getName(), company.getTicker());
                            }

                            long idCompany = companyDao.getIdCompanyByFigi(figi);
                            List<CandleDto> candleDtos = figiAndCandles.get(figi);

                            candleDao.saveCandles(candleDtos, idCompany);
                            log.info(CANDLE_LIST_SIZE_FOR_FIGI, figi, candleDtos.size());
                        }
                );
        log.info(CANDLES_PROCESSED_FOR_FIGI_COUNT, figiAndCandles.keySet().size());
    }

    @Transactional
    @Override
    public void calculateStatsMetrics() {
        companyDao
                .getIdsCompany()
                .forEach(id -> {
                            Metric statsMetric = candleDao.calculateStandardDeviationAndAverage(id);
                            metricDao.save(id, statsMetric);

                            log.info(COMPANY_STATS_SAVED, id, statsMetric.getStandardDeviation(), statsMetric.getAverage());
                        }
                );
    }

    @Transactional
    @Override
    public void calculateStatsMetricsByIrvin() {
        companyDao
                .getIdsCompany()
                .forEach(id -> {
                            Metric tempMetric = candleDao.calculateStandardDeviationAndAverage(id);
                            MetricByIrvin newMetric = new MetricByIrvin(
                                    tempMetric.getStandardDeviation(),
                                    tempMetric.getAverage(),
                                    candleDao.calculateCriticalValue(id)
                            );

                            metricDao.save(id, newMetric);
                            log.info(COMPANY_STATS_BY_IRVIN_SAVED, id, newMetric.getStandardDeviation(), newMetric.getAverage(), newMetric.getCriticalValue());
                        }
                );
    }
}
