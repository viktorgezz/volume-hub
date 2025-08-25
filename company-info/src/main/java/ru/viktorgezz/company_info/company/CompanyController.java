package ru.viktorgezz.company_info.company;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.viktorgezz.company_info.company.intf.CompanyService;
import ru.viktorgezz.company_info.company.intf.Converter;
import ru.viktorgezz.company_info.exception.BusinessException;


import java.util.List;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
@Tag(name = "Company Controller", description = "API для управления компаниями: получение, добавление, обновление и удаление данных о компаниях")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/figi")
    @Operation(summary = "Получить все FIGI", description = "Возвращает список всех доступных FIGI компаний из базы данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список FIGI успешно получен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public List<String> getFigis() {
        return companyService.getAllFigis();
    }

    @GetMapping
    public List<CompanyRsDto> getCompanies() {
        return Converter.convert(companyService.getAllCompany());
    }

    @GetMapping("/name/{figi}")
    @Operation(summary = "Получить имя компании по FIGI", description = "Возвращает имя компании по указанному FIGI. Если компания не найдена, бросается исключение")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Имя компании успешно получено",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BusinessException.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public String getNameByFigi(@PathVariable final String figi) {
        return companyService.findNameByFigi(figi);
    }

    @GetMapping("/{figi}")
    @Operation(summary = "Получить компанию по figi", description = "Возвращает полную информацию о компании по указанному figi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Компания успешно получена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyRsDto.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BusinessException.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public CompanyRsDto getCompanyByFigi(@PathVariable final String figi) {
        return Converter.convert(companyService.findCompanyByFigi(figi));
    }

    @GetMapping("/ticker/{ticker}")
    @Operation(summary = "Получить компанию по figi", description = "Возвращает полную информацию о компании по указанному figi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Компания успешно получена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyRsDto.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BusinessException.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public CompanyRsDto getCompanyByTicker(@PathVariable final String ticker) {
        return Converter.convert(companyService.findCompanyByTicker(ticker));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить новую компанию", description = "Добавляет новую компанию в базу данных на основе DTO. Возвращает сообщение об успехе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Компания успешно добавлена",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные (валидация не пройдена)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BusinessException.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public String addCompany(
            @RequestBody
            @Valid final CompanyRqDto companyRqDto
    ) {
        companyService.addCompany(Converter.convert(companyRqDto));
        return "Компания добавлена";
    }

    @DeleteMapping("/{ticker}")
    @Operation(summary = "Удалить компанию по тикеру", description = "Удаляет компанию по указанному тикеру. Если компания не найдена, бросается исключение")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Компания успешно удалена",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BusinessException.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public String deleteCompanyByTicker(@PathVariable final String ticker) {
        companyService.deleteCompanyByTicker(ticker);
        return "Компания удалена " + ticker;
    }

    @PatchMapping("/figi")
    @Operation(summary = "Обновить FIGI по тикеру", description = "Обновляет FIGI компании по указанному тикеру. Если компания не найдена, бросается исключение")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FIGI успешно обновлён",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BusinessException.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public String updateFigiByTicker(
            @RequestParam final String figi,
            @RequestParam final String ticker
    ) {
        companyService.updateFigiByTicker(figi, ticker);
        return "Обновлен figi на " + figi;
    }

    @PatchMapping("/name")
    @Operation(summary = "Обновить имя по тикеру", description = "Обновляет имя компании по указанному тикеру. Если компания не найдена, бросается исключение")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Имя успешно обновлено",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BusinessException.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public String updateNameByTicker(
            @RequestParam final String name,
            @RequestParam final String ticker
    ) {
        companyService.updateNameByTicker(name, ticker);
        return "Обновлено имя на " + name;
    }

    @PatchMapping("/ticker")
    @Operation(summary = "Обновить тикер", description = "Обновляет тикер компании с старого на новый. Если компания не найдена, бросается исключение")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тикер успешно обновлён",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BusinessException.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public String updateTicker(
            @RequestParam final String oldTicker,
            @RequestParam final String newTicker
    ) {
        companyService
                .updateTickerByTicker(
                        oldTicker,
                        newTicker
                );
        return "Обновлен тикер на " + newTicker;
    }
}
