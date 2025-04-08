package ru.viktorgezz.api_T_connector.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record CustomCandle(
        BigDecimal open,
        BigDecimal close,
        BigDecimal high,
        BigDecimal low,
        long volume,
        Timestamp time
) {
}
