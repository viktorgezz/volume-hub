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
@RequestMapping("/candle")
public class CandleController {

    private final CandleStreamService candleStreamService;
    private final HistoricalCandleMarketService dataMarketHistoric;

    @Autowired
    public CandleController(
            CandleStreamService candleStreamService,
            HistoricalCandleMarketService dataMarketHistoric
    ) {
        this.candleStreamService = candleStreamService;
        this.dataMarketHistoric = dataMarketHistoric;
    }

    @GetMapping("/minute/now")
    public String runLogVolume() {
        this.candleStreamService.streamLatestMinuteCandles();
        return "Успех";
    }

    @GetMapping("/minute/disconnection")
    public String disconnect() {
        candleStreamService.cancelStream();
        return "Успешная отмена подписки на свечи";
    }

    @GetMapping("minute/for-last-day")
    public Map<String, List<CustomCandle>> sendMinuteCandlesForLastDay() {
        return dataMarketHistoric.getMinuteCandlesForLastDayAllFigis();
    }

    @GetMapping("minute/for-last-hour/{figi}")
    public Map<String, List<CustomCandle>> sendMinuteCandlesForLastHourByFigi(
            @PathVariable("figi") String figi
    ) {
        return dataMarketHistoric.getMinuteCandlesForLastHourByFigi(figi);
    }

    @GetMapping("/day/{figi}")
    public CustomCandle sendDayCandleCurrent(@PathVariable("figi") String figi) {
        return dataMarketHistoric.getDayCandleCurrent(figi);
    }
}

