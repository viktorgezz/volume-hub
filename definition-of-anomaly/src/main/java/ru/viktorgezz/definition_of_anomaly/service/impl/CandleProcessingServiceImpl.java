package ru.viktorgezz.definition_of_anomaly.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.viktorgezz.definition_of_anomaly.client.ClientInvest;
import ru.viktorgezz.definition_of_anomaly.dao.CandleDao;
import ru.viktorgezz.definition_of_anomaly.dao.CompanyDao;
import ru.viktorgezz.definition_of_anomaly.dao.MetricDao;
import ru.viktorgezz.definition_of_anomaly.dto.CandleDto;
import ru.viktorgezz.definition_of_anomaly.service.interf.CandleProcessingService;
import ru.viktorgezz.definition_of_anomaly.model.Metric;

import java.util.List;
import java.util.Map;

@Service
public class CandleProcessingServiceImpl implements CandleProcessingService {

    private static final Logger log = LoggerFactory.getLogger(CandleProcessingServiceImpl.class);

    private final ClientInvest clientInvest;
    private final CompanyDao companyDao;
    private final CandleDao candleDao;
    private final MetricDao metricDao;


    @Autowired
    public CandleProcessingServiceImpl(
            ClientInvest clientInvest,
            CompanyDao companyDao,
            CandleDao candleDao,
            MetricDao metricDao) {
        this.clientInvest = clientInvest;
        this.companyDao = companyDao;
        this.candleDao = candleDao;
        this.metricDao = metricDao;
    }

    @Override
    public void uploadCandlesForLastDay() {
        Map<String, List<CandleDto>> figiAndCandles = clientInvest.fetchMinuteCandlesForLastDay();
        figiAndCandles
                .keySet()
                .forEach(figi -> {
                            if (!companyDao.isCompanyPresent(figi)) {
                                String nameCompany = clientInvest.fetchNameCompanyByFigi(figi);
                                companyDao.save(figi, nameCompany);
                                log.info("figi: {} and name: {} add in Company", figi, nameCompany);
                            }

                            long idCompany = companyDao.getIdCompanyByFigi(figi);
                            List<CandleDto> candleDtos = figiAndCandles.get(figi);

                            candleDao.saveUniqueCandles(candleDtos, idCompany);
                            log.info("figi: {}, size list candles: {}", figi, candleDtos.size());
                        }
                );
        log.info("Свечи обработаны количество figi: {}", figiAndCandles.keySet().size());
    }

    @Override
    public void calculateStatsMetrics() {
        companyDao
                .getIdsCompany()
                .forEach(id -> {
                            Metric statsMetric = candleDao.calculateStandardDeviationAndAverage(id);
                            metricDao.save(id, statsMetric);

                            log.info(
                                    "Company: {} is add standard deviation: {} and average: {} in table",
                                    id,
                                    statsMetric.standardDeviation(),
                                    statsMetric.average());
                        }
                );
    }
}
