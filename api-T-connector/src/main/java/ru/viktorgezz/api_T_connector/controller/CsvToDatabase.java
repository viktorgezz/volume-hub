package ru.viktorgezz.api_T_connector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.api_T_connector.service.interf.ShareService;

@RestController
public class CsvToDatabase {

    private final ShareService shareService;

    @Autowired
    public CsvToDatabase(ShareService shareService) {
        this.shareService = shareService;
    }

    @PostMapping("/import")
    public String importCSV() {
        return shareService.importCsvToFigisTable()
                .orElseThrow(() -> new RuntimeException("Ошибка импорта в базу данных из csv"));
    }
}
