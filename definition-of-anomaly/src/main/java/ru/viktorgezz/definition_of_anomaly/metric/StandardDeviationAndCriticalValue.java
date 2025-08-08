package ru.viktorgezz.definition_of_anomaly.metric;

import java.math.BigDecimal;

public record StandardDeviationAndCriticalValue(
        BigDecimal StandardDeviation,
        BigDecimal CriticalValue
) {
}
