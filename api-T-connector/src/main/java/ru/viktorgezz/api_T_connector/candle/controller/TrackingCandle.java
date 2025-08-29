package ru.viktorgezz.api_T_connector.candle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.api_T_connector.candle.service.interf.CandleStreamService;

@RestController
@RequestMapping("/connector/public/api/v1")
@Tag(name = "Онлайн-трекинг свечей", description = "API для подписки и отмены подписки на онлайн-свечи")
public class TrackingCandle {

    private final CandleStreamService candleStreamService;

    @Autowired
    public TrackingCandle(CandleStreamService candleStreamService) {
        this.candleStreamService = candleStreamService;
    }

    @Operation(
            summary = "Начать подписку на минутные свечи",
            description = "Запускает процесс получения актуальных минутных свечей в реальном времени",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Подписка успешно запущена")
            }
    )
    @GetMapping("/minute/now")
    public String startProcessOfTrackingMinuteCandles() {
        this.candleStreamService.streamLatestMinuteCandles();
        return "Успех";
    }

    @Operation(
            summary = "Отписаться от получения минутных свечей",
            description = "Останавливает процесс получения актуальных минутных свечей в реальном времени",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Подписка успешно отменена")
            }
    )
    @GetMapping("/minute/disconnection")
    public String disconnectProcessOfTrackingMinuteCandles() {
        candleStreamService.cancelStream();
        return "Успешная отмена подписки на свечи";
    }
}
