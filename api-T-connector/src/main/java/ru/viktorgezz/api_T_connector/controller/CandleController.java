package ru.viktorgezz.api_T_connector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/candle")
public class CandleController {

    private static final Logger log = LoggerFactory.getLogger(CandleController.class);
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

    @GetMapping("/minute/now")
    public String runLogVolume() {
        this.candleStreamService.streamLatestMinuteCandles(this.figis);
        return "Успех";
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

