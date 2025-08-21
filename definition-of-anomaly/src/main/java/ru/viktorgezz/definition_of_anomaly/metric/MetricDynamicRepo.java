package ru.viktorgezz.definition_of_anomaly.metric;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.viktorgezz.definition_of_anomaly.metric.model.MetricDynamic;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface MetricDynamicRepo extends CrudRepository<MetricDynamic, Long> {

    @Modifying
    @Query("UPDATE metric_dynamic " +
            "SET coefficient = coefficient + :summand, updated_at = CURRENT_TIMESTAMP " +
            "WHERE id_company = :idCompany")
    void updateCoefficientBy(Long idCompany, BigDecimal summand);

    @Modifying
    @Query("INSERT INTO metric_dynamic (id_company, coefficient, updated_at) " +
            "VALUES (:idCompany, :coefficient, :updatedAt) " +
            "ON CONFLICT (id_company) DO NOTHING")
    void insertIfNotExists(Long idCompany, BigDecimal coefficient, Timestamp updatedAt);

    @Query("SELECT * " +
            "FROM metric_dynamic " +
            "WHERE id_company = :idCompany")
    Optional<MetricDynamic> getMetricDynamicByIdCompany(Long idCompany);
}
