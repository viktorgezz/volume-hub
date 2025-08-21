package ru.viktorgezz.definition_of_anomaly.metric.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Table("metric_dynamic")
public class MetricDynamic {

    @Id
    private Long id;
    private Long idCompany;
    private BigDecimal coefficient;
    private Timestamp updatedAt;

    public MetricDynamic() {
    }

    public MetricDynamic(Long idCompany, BigDecimal coefficient, Timestamp updatedAt) {
        this.idCompany = idCompany;
        this.coefficient = coefficient;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MetricDynamic that = (MetricDynamic) object;
        return Objects.equals(id, that.id) && Objects.equals(idCompany, that.idCompany) && Objects.equals(coefficient, that.coefficient) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idCompany, coefficient, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(Long idCompany) {
        this.idCompany = idCompany;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
