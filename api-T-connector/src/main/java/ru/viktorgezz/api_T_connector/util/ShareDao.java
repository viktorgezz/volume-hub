package ru.viktorgezz.api_T_connector.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class ShareDao {

    private static final Logger log = LoggerFactory.getLogger(ShareDao.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ShareDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> loadFigis() {
        final String sql = "SELECT * FROM _share";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("figi"));
    }

    public String getCompanyNameByFigi(String figi) {
        final String sql = "SELECT company FROM _share WHERE figi = ?";
        return jdbcTemplate.queryForObject(sql, String.class, figi);
    }

    public Optional<String> importCsvToFigisTable() {
        final String sqlAddCompany = "INSERT INTO _share (figi, company) VALUES(?, ?)";
        final String sqlSelectFigi = "SELECT figi FROM _share WHERE figi = ?";
        final String fileName = "figi.csv";

        ClassPathResource resource = new ClassPathResource(fileName);
        try (CSVReader csvReader = new CSVReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
        ) {
            String[] line;

            while ((line = csvReader.readNext()) != null) {
                if (line.length >= 2) {
                    String figi = line[1].trim();
                    String company = line[0].trim();

                    String figiFound = null;
                    try {
                        figiFound = jdbcTemplate.queryForObject(
                                sqlSelectFigi,
                                String.class,
                                figi);
                    } catch (EmptyResultDataAccessException e) {
                        log.info("company not founded: {}, {}", company, figi);
                    }
                    if (figiFound == null || figiFound.isEmpty()) {
                        jdbcTemplate.update(sqlAddCompany, figi, company);
                    } else {
                        log.info("Компания уже есть: {}, {}", company, figi);
                    }

                } else {
                    log.error("Некорректная строка в csv: {}", String.join(",", line));
                    return Optional.empty();
                }
            }
            log.info("Данные из CSV успешно импортированы в таблицу _share.");
        } catch (IOException | CsvValidationException e) {
            log.error("Ошибка при чтении CSV-файла: {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.of("Данные успешно загружены");
    }

    public void addFigis() {
        final String sql = "INSERT INTO _share (figi, company) VALUES(?, ?)";
        jdbcTemplate.update(sql, "BBG004S683W7", "Аэрофлот");
    }

//    public void executeQuery() {
//        String sql = "SELECT * FROM your_table";
//        jdbcTemplate.query(sql, (rs, rowNum) -> {
//            System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
//            return null;
//        });
//    }
//
//    // Пример выполнения INSERT-запроса
//    public void executeUpdate() {
//        String sql = "INSERT INTO your_table (name) VALUES (?)";
//        jdbcTemplate.update(sql, "Example");
//    }
}
