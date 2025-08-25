package ru.viktorgezz.definition_of_anomaly.metric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.metric.model.Metric;
import ru.viktorgezz.definition_of_anomaly.metric.model.MetricByIrvin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class MetricDao {

    private final static String NAME_TABLE_METRIC = "metric";
    private static final Logger log = LoggerFactory.getLogger(MetricDao.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MetricDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(
            Long idCompany,
            MetricByIrvin statsMetric
    ) {
        final String sql = String.format("INSERT INTO %s (id_company, standard_deviation, average, updated_at, critical_value) " +
                        "VALUES (?, ?, ?, ?, ?) " +
                        "ON CONFLICT (id_company) " +
                        "DO UPDATE SET " +
                        "standard_deviation = EXCLUDED.standard_deviation, " +
                        "updated_at = CURRENT_TIMESTAMP, " +
                        "average = EXCLUDED.average, " +
                        "critical_value = EXCLUDED.critical_value;",
                NAME_TABLE_METRIC
        );
        jdbcTemplate.update(
                sql,
                idCompany,
                statsMetric.getStandardDeviation(),
                statsMetric.getAverage(),
                Timestamp.valueOf(LocalDateTime.now()),
                statsMetric.getCriticalValue()
        );
    }

    public BigDecimal computeAbsoluteDifference(
            Long idCompany,
            Long volume
    ) {
        final String sql = String.format("""
                SELECT ? - average 
                FROM %s 
                WHERE id_company = ?;
                """, NAME_TABLE_METRIC);

        try {
            return Objects.requireNonNull(
                    jdbcTemplate.queryForObject(
                            sql,
                            BigDecimal.class,
                            volume,
                            idCompany
                    ));
        } catch (EmptyResultDataAccessException | NullPointerException e) {
            log.warn("error message: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getStandardDeviation(long idCompany) {
        final String sql = String.format("SELECT standard_deviation FROM %s WHERE id_company = ?;",
                NAME_TABLE_METRIC);
        return Objects
                .requireNonNull(jdbcTemplate
                        .queryForObject(sql, BigDecimal.class, idCompany))
                .setScale(3, RoundingMode.HALF_UP);
    }

    public BigDecimal getCriticalValue(long idCompany) {
        final String sql = String.format("SELECT critical_value FROM %s WHERE id_company = ?;",
                NAME_TABLE_METRIC);
        return Objects
                .requireNonNull(jdbcTemplate
                        .queryForObject(sql, BigDecimal.class, idCompany))
                .setScale(6, RoundingMode.HALF_UP);
    }
}
