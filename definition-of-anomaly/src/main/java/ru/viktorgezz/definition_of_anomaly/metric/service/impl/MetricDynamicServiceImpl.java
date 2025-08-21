package ru.viktorgezz.definition_of_anomaly.metric.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.definition_of_anomaly.company.CompanyService;
import ru.viktorgezz.definition_of_anomaly.metric.MetricDynamicRepo;
import ru.viktorgezz.definition_of_anomaly.metric.model.MetricDynamic;
import ru.viktorgezz.definition_of_anomaly.metric.service.intrf.MetricDynamicService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MetricDynamicServiceImpl implements MetricDynamicService {

    private final static BigDecimal VALUE_DOWNGRADE = new BigDecimal("-0.1");
    private final static BigDecimal VALUE_UPGRADE = new BigDecimal("0.1");
    private final static long MILLIS_IN_24_HOURS = 24 * 60 * 60 * 1000L;
    private static final Logger log = LoggerFactory.getLogger(MetricDynamicServiceImpl.class);

    private final MetricDynamicRepo metricDynamicRepo;
    private final CompanyService companyService;

    @Autowired
    public MetricDynamicServiceImpl(
            MetricDynamicRepo metricDynamicRepo, CompanyService companyService
    ) {
        this.metricDynamicRepo = metricDynamicRepo;
        this.companyService = companyService;
    }

    @Override
    @Transactional
    public void decreaseCoefficient(Long idCompany) {
        MetricDynamic metric = findCoefficientByIdCompany(idCompany)
                .orElseThrow();

        if (isGreaterThanOne(metric.getCoefficient())
                && hasMoreThan24HoursPassedSince(metric.getUpdatedAt())
        ) {
            metricDynamicRepo.updateCoefficientBy(idCompany, VALUE_DOWNGRADE);
            log.info("Снижен коэффициент у компании c id: {} на {}", idCompany, VALUE_DOWNGRADE);
        }
    }

    @Override
    @Transactional
    public void increaseCoefficient(Long idCompany) {
        metricDynamicRepo.updateCoefficientBy(idCompany, VALUE_UPGRADE);
        log.info("Повышен коэффициент у компании c id: {} на {}", idCompany, VALUE_UPGRADE);
    }

    @Override
    public Optional<MetricDynamic> findCoefficientByIdCompany(Long idCompany) {
        return metricDynamicRepo
                .getMetricDynamicByIdCompany(idCompany);
    }

    @Override
    @Transactional
    public void loadDataMetricDynamic() {
        companyService.getIdsCompany().forEach(idCompany ->
                save(new MetricDynamic(
                                idCompany,
                                BigDecimal.ONE,
                                Timestamp.valueOf(LocalDateTime.now())
                        )
                )
        );
    }

    @Override
    @Transactional
    public void save(MetricDynamic metricDynamic) {
            metricDynamicRepo.insertIfNotExists(
                    metricDynamic.getIdCompany(),
                    metricDynamic.getCoefficient(),
                    Timestamp.valueOf(LocalDateTime.now())
            );
    }

    private static boolean isGreaterThanOne(BigDecimal coefficient) {
        return coefficient.compareTo(new BigDecimal("1")) > 0;
    }

    private static boolean hasMoreThan24HoursPassedSince(Timestamp updatedAt) {
        return Timestamp.valueOf(LocalDateTime.now()).after(new Timestamp(updatedAt.getTime() + MILLIS_IN_24_HOURS));
    }
}