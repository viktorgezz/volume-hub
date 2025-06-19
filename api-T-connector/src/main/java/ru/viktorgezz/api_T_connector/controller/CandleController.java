package ru.viktorgezz.api_T_connector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.api_T_connector.model.CustomCandle;
import ru.viktorgezz.api_T_connector.service.interf.CandleStreamService;
import ru.viktorgezz.api_T_connector.service.interf.HistoricalCandleMarketService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/candle")
public class CandleController {

    private final HistoricalCandleMarketService dataMarketHistoric;

    @Autowired
    public CandleController(
            HistoricalCandleMarketService dataMarketHistoric
    ) {
        this.dataMarketHistoric = dataMarketHistoric;
    }

    @GetMapping("/minute/for-last-day")
    public Map<String, List<CustomCandle>> sendMinuteCandlesForLastDay() {
        return dataMarketHistoric.getMinuteCandlesForLastDayAllFigis();
    }

    @GetMapping("/minute/for-last-hour/{figi}")
    public List<CustomCandle> sendMinuteCandlesForLastHourByFigi(
            @PathVariable("figi") String figi
    ) {
        return dataMarketHistoric.getMinuteCandlesForLastHourByFigi(figi);
    }

    @GetMapping("/last-two-day/{figi}")
    public List<CustomCandle> sendLastTwoDaysCandle(@PathVariable("figi") String figi) {
        return dataMarketHistoric.getLastTwoDaysCandle(figi);
    }

    @GetMapping("/last-two-minute/{figi}")
    public List<CustomCandle> sendLastTwoMinuteCandle(@PathVariable("figi") String figi) {
        return dataMarketHistoric.getLastTwoMinuteCandle(figi);
    }
}

