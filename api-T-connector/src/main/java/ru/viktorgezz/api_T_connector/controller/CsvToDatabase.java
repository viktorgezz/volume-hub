package ru.viktorgezz.api_T_connector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.api_T_connector.util.ShareDao;

@RestController
public class CsvToDatabase {

    private final ShareDao shareDao;

    @Autowired
    public CsvToDatabase(ShareDao shareDao) {
        this.shareDao = shareDao;
    }

    @PostMapping("/import")
    public String importCSV() {
        return shareDao.importCsvToFigisTable()
                .orElseThrow(() -> new RuntimeException("Ошибка импорта в базу данных из csv"));
    }
}
