package ru.viktorgezz.company_info.rabbitmq;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CompanyMessage {

    private String name;
    private String tickerUpdate;
    private String figi;

    private String lookupTicker; // Тикер по которому идет поиск для обновления значений

}
