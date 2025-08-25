package ru.viktorgezz.api_T_connector.candle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.api_T_connector.candle.service.interf.CandleStreamService;

@RestController
@RequestMapping("/connector/public/api/v1")
public class TrackingCandle {

    private final CandleStreamService candleStreamService;

    @Autowired
    public TrackingCandle(CandleStreamService candleStreamService) {
        this.candleStreamService = candleStreamService;
    }

    @GetMapping("/minute/now")
    public String startProcessOfTrackingMinuteCandles() {
        this.candleStreamService.streamLatestMinuteCandles();
        return "Успех";
    }

    @GetMapping("/minute/disconnection")
    public String disconnectProcessOfTrackingMinuteCandles() {
        candleStreamService.cancelStream();
        return "Успешная отмена подписки на свечи";
    }
}
