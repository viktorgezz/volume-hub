package ru.viktorgezz.definition_of_anomaly.metric;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Metric {

    private final BigDecimal standardDeviation;
    private final BigDecimal average;

    public Metric(
            final BigDecimal standardDeviation,
            final BigDecimal average) {
        this.standardDeviation = standardDeviation.setScale(3, RoundingMode.HALF_UP);
        this.average = average.setScale(6, RoundingMode.HALF_UP);
    }

    public BigDecimal getStandardDeviation() {
        return standardDeviation;
    }

    public BigDecimal getAverage() {
        return average;
    }
}
