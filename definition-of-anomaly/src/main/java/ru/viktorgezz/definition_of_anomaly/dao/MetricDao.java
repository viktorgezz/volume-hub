package ru.viktorgezz.definition_of_anomaly.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.model.Metric;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class MetricDao {

    private final static String NAME_TABLE_CANDLE = "metric";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MetricDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(
            Long idCompany,
            Metric statsMetric
    ) {
        final String sql = String.format("INSERT INTO %s (id_company, standard_deviation, average, updated_at) " +
                        "VALUES (?, ?, ?, ?) " +
                        "ON CONFLICT (id_company) " +
                        "DO UPDATE SET " +
                        "standard_deviation = EXCLUDED.standard_deviation, " +
                        "updated_at = CURRENT_TIMESTAMP, " +
                        "average = EXCLUDED.average;",
                NAME_TABLE_CANDLE
        );
        jdbcTemplate.update(
                sql,
                idCompany, statsMetric.standardDeviation(),
                statsMetric.average(), Timestamp.valueOf(LocalDateTime.now())
        );
    }

    public BigDecimal calculateCurrentZScore(long volume, long idCompany) {
        final String sql = String.format(
                "SELECT COALESCE((? - average) / NULLIF(standard_deviation, 0), 0) " +
                "FROM %s " +
                "WHERE id_company = ?; ",
                NAME_TABLE_CANDLE);
        return Objects
                .requireNonNull(jdbcTemplate
                        .queryForObject(sql, BigDecimal.class, volume, idCompany))
                .setScale(3, RoundingMode.HALF_UP);
    }

    public BigDecimal getZScoreFromTable(long idCompany) {
        final String sql = String.format("SELECT z_score FROM %s WHERE id_company = ?;",
                NAME_TABLE_CANDLE);
        return Objects
                .requireNonNull(jdbcTemplate
                        .queryForObject(sql, BigDecimal.class, idCompany))
                .setScale(3, RoundingMode.HALF_UP);

    }
}
