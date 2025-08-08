package ru.viktorgezz.definition_of_anomaly.metric;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MetricByIrvin extends Metric  {

    private final BigDecimal criticalValue;

    public MetricByIrvin(
            BigDecimal standardDeviation,
            BigDecimal average,
            BigDecimal criticalValue) {
        super(standardDeviation, average);
        this.criticalValue = criticalValue.setScale(6, RoundingMode.HALF_UP);
    }

    public BigDecimal getCriticalValue() {
        return criticalValue;
    }
}
