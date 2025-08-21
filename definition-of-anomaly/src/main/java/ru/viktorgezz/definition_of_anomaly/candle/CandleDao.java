package ru.viktorgezz.definition_of_anomaly.candle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.candle.model.AbstractCandle;
import ru.viktorgezz.definition_of_anomaly.candle.model.CandleDto;
import ru.viktorgezz.definition_of_anomaly.metric.model.Metric;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Component
public class CandleDao {

    private static final Logger log = LoggerFactory.getLogger(CandleDao.class);

    private final static String NAME_TABLE_CANDLE = "candle";
    private static final String DATA_NOT_FOUND_FOR_COMPANY_ID = "Не найдено данных для id_company:: {} {}";
    private static final String DB_ERROR_CALCULATING_STD_DEV = "Ошибка базы данных при вычислении стандартного отклонения для id_company: {} {}";
    private static final String FAILED_TO_CALCULATE_STD_DEV_DB_ERROR = "Не удалось рассчитать стандартное отклонение из-за ошибки базы данных";
    private static final String UNEXPECTED_ERROR_CALCULATING_STD_DEV = "Непредвиденная ошибка при расчете стандартного отклонения";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CandleDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveCandles(List<CandleDto> candleDtos, long idCompany) {
        candleDtos
                .forEach(c -> {
                    saveCandle(c, idCompany);
                });
    }

    public void saveCandle(AbstractCandle candle, long idCompany) {
            final String sql = String.format(
                    "INSERT INTO %s (id_company, open, close, high, low, volume, time) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                            "ON CONFLICT (id_company, time) DO NOTHING",
                    NAME_TABLE_CANDLE);
            jdbcTemplate.update(
                    sql,
                    idCompany, candle.getOpen(), candle.getClose(), candle.getHigh(), candle.getLow(),
                    candle.getVolume(), candle.getTime());
    }

    public BigDecimal computeCriticalValue(Long idCompany) {
        final String sql = String.format("""
                    WITH num_of_entries AS (
                        SELECT COUNT(id) AS count 
                        FROM %s 
                        WHERE id_company = ?
                    )
                    SELECT CASE 
                        WHEN num_of_entries.count > 1 
                        THEN SQRT(2 * LOG10(num_of_entries.count) 
                                  - LOG10(LOG10(num_of_entries.count)) 
                                  - LOG10(4 * PI())) 
                        ELSE NULL 
                    END AS result
                    FROM num_of_entries;
                """, NAME_TABLE_CANDLE);
        try {
            return Objects
                    .requireNonNull(jdbcTemplate.queryForObject(
                            sql,
                            BigDecimal.class,
                            idCompany
                    )).setScale(6, RoundingMode.HALF_UP);
        } catch (EmptyResultDataAccessException | NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public Metric computeStandardDeviationAndAverage(Long idCompany) {
        try {
            final String sql = String.format("""
                            WITH sorted_volumes AS (
                                SELECT volume,
                                       NTILE(4) OVER (ORDER BY volume) AS quartile
                                FROM %s
                                WHERE id_company = ?
                            ),
                            filtered_volumes AS (
                                SELECT volume
                                FROM sorted_volumes
                                WHERE quartile NOT IN (1, 4)
                            ),
                            average AS (
                                SELECT AVG(volume) AS avg_volume
                                FROM filtered_volumes
                            ),
                            deviations AS (
                                SELECT POWER(fv.volume - a.avg_volume, 2) AS squared_deviation
                                FROM filtered_volumes fv, average a
                            ),
                            sum_squared_deviations AS (
                                SELECT SUM(squared_deviation) AS total_squared_deviation
                                FROM deviations
                            ),
                            count_filtered AS (
                                SELECT COUNT(*) AS n
                                FROM filtered_volumes
                            )
                            SELECT SQRT(total_squared_deviation / n) AS standard_deviation, avg_volume
                            FROM sum_squared_deviations, count_filtered, average;""",
                    NAME_TABLE_CANDLE
            );

            Metric metric = jdbcTemplate.queryForObject(
                    sql,
                    (resultSet, rowNum) -> {
                        return new Metric(
                                resultSet.getBigDecimal("standard_deviation"),
                                resultSet.getBigDecimal("avg_volume")
                        );
                    },
                    idCompany
            );

            return Objects.requireNonNull(metric);
        } catch (IncorrectResultSizeDataAccessException | NullPointerException e) {
            log.error(DATA_NOT_FOUND_FOR_COMPANY_ID, idCompany, e.getMessage());
            return new Metric(BigDecimal.ZERO, BigDecimal.ZERO);
        } catch (DataAccessException e) {
            log.error(DB_ERROR_CALCULATING_STD_DEV, idCompany, e.getMessage());
            throw new RuntimeException(FAILED_TO_CALCULATE_STD_DEV_DB_ERROR, e);
        } catch (Exception e) {
            throw new RuntimeException(UNEXPECTED_ERROR_CALCULATING_STD_DEV, e);
        }
    }
}

