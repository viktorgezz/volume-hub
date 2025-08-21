package ru.viktorgezz.definition_of_anomaly.candle.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.definition_of_anomaly.candle.intf.CandleDataClient;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleService;
import ru.viktorgezz.definition_of_anomaly.candle.model.CandleDto;
import ru.viktorgezz.definition_of_anomaly.company.CompanyService;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleProcessingService;

import java.util.List;
import java.util.Map;

@Service
public class CandleProcessingServiceImpl implements CandleProcessingService {

    private static final Logger log = LoggerFactory.getLogger(CandleProcessingServiceImpl.class);

    private static final String CANDLE_LIST_SIZE_FOR_FIGI = "figi: {}, size list candles: {}";
    private static final String CANDLES_PROCESSED_FOR_FIGI_COUNT = "Свечи обработаны количество figi: {}";

    private final CandleDataClient candleDataClient;

    private final CandleService candleService;
    private final CompanyService companyService;


    @Autowired
    public CandleProcessingServiceImpl(
            CandleDataClient candleDataClient,
            CandleService candleService,
            CompanyService companyService
    ) {
        this.candleDataClient = candleDataClient;
        this.candleService = candleService;
        this.companyService = companyService;
    }

    @Transactional
    @Override
    public void uploadCandlesForLastDay() {
        Map<String, List<CandleDto>> figiAndCandles = candleDataClient.fetchMinuteCandlesForLastDay();
        figiAndCandles
                .keySet()
                .forEach(figi -> {
                            try {
                                companyService.loadCompanyIfNotPresent(figi);
                                List<CandleDto> candleDtos = figiAndCandles.get(figi);
                                candleService.saveCandles(
                                        candleDtos,
                                        companyService.getIdCompanyByFigi(figi)
                                ); // исправить сохранение свечей, чтобы не уникальные не добавлялись и чтобы не ломалась транзакция

                                log.info(CANDLE_LIST_SIZE_FOR_FIGI, figi, candleDtos.size());
                            } catch (RuntimeException e) {
                                log.error("Ошибка при обработке свече {}: {} ", figi, e.getMessage());
                            }
                        }
                );
        log.info(CANDLES_PROCESSED_FOR_FIGI_COUNT, figiAndCandles.keySet().size());
    }
}
