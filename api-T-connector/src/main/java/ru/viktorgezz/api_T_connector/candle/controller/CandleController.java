package ru.viktorgezz.api_T_connector.candle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.api_T_connector.candle.CustomCandle;
import ru.viktorgezz.api_T_connector.candle.service.interf.HistoricalCandleMarketService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/candle")
@Tag(name = "Исторические свечи", description = "API для получения исторических данных по свечам")
public class CandleController {

    private final HistoricalCandleMarketService dataMarketHistoric;

    @Autowired
    public CandleController(
            HistoricalCandleMarketService dataMarketHistoric
    ) {
        this.dataMarketHistoric = dataMarketHistoric;
    }

    @Operation(
            summary = "Минутные свечи за последний день",
            description = "Возвращает минутные свечи за последние 24 часа по всем FIGI",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение данных",
                            content = @Content(schema = @Schema(implementation = CustomCandle.class)))
            }
    )
    @GetMapping("/minute/for-last-day")
    public Map<String, List<CustomCandle>> sendMinuteCandlesForLastDay() {
        return dataMarketHistoric.getMinuteCandlesForLastDayAllFigis();
    }

    @Operation(
            summary = "Минутные свечи за последний час по FIGI",
            description = "Возвращает список минутных свечей за последний час для указанного FIGI",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение данных",
                            content = @Content(schema = @Schema(implementation = CustomCandle.class))),
            }
    )
    @GetMapping("/minute/for-last-hour/{figi}")
    public List<CustomCandle> sendMinuteCandlesForLastHourByFigi(
            @PathVariable("figi") String figi
    ) {
        return dataMarketHistoric.getMinuteCandlesForLastHourByFigi(figi);
    }

    @Operation(
            summary = "Дневные свечи за последние два дня",
            description = "Возвращает список свечей за последние два дня по указанному FIGI",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение данных",
                            content = @Content(schema = @Schema(implementation = CustomCandle.class))),
            }
    )
    @GetMapping("/last-two-day/{figi}")
    public List<CustomCandle> sendLastTwoDaysCandle(@PathVariable("figi") String figi) {
        return dataMarketHistoric.getLastTwoDaysCandle(figi);
    }
}

