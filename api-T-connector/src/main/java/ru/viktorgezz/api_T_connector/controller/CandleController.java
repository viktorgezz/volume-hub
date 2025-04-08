package ru.viktorgezz.api_T_connector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.api_T_connector.model.CustomCandle;
import ru.viktorgezz.api_T_connector.service.interf.CandleStreamService;
import ru.viktorgezz.api_T_connector.service.interf.HistoricalCandleMarketService;
import ru.viktorgezz.api_T_connector.util.FigiList;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/minute-candles")
public class CandleController {

    private final CandleStreamService candleStreamService;
    private final HistoricalCandleMarketService dataMarketHistoric;

    private final List<String> figis;

    @Autowired
    public CandleController(
            CandleStreamService candleStreamService,
            HistoricalCandleMarketService dataMarketHistoric,
            FigiList figiListObject
    ) {
        this.candleStreamService = candleStreamService;
        this.dataMarketHistoric = dataMarketHistoric;
        this.figis = figiListObject.getFigis();
    }

    @GetMapping("/now")
    public String runLogVolume() {
        this.candleStreamService.streamLatestMinuteCandles(this.figis);
        return "Успех";
    }

    @GetMapping("/for-last-day")
    public Map<String, List<CustomCandle>> getMinuteCandlesForLastDay() {
        return dataMarketHistoric.getMinuteCandlesForLastDayAllFigis();
    }

    @GetMapping("/for-last-hour/{figi}")
    public Map<String, List<CustomCandle>> getMinuteCandlesForLastHourByFigi(
            @PathVariable("figi") String figi
    ) {
        return dataMarketHistoric.getMinuteCandlesForLastHourByFigi(figi);
    }
}

