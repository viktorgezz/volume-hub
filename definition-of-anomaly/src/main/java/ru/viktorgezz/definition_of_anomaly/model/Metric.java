package ru.viktorgezz.definition_of_anomaly.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Metric(BigDecimal standardDeviation, BigDecimal average) {

    public Metric(
            BigDecimal standardDeviation,
            BigDecimal average) {
        this.standardDeviation = standardDeviation.setScale(3, RoundingMode.HALF_UP);
        this.average = average.setScale(6, RoundingMode.HALF_UP);
    }

    public BigDecimal standardDeviation() {
        return standardDeviation;
    }

    public BigDecimal average() {
        return average;
    }
}
