package ru.viktorgezz.api_T_connector.dao;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.viktorgezz.api_T_connector.model.Share;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
public class ShareDao {

    private static final Logger log = LoggerFactory.getLogger(ShareDao.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ShareDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getCompanyNameByFigi(String figi) {
        final String sql = "SELECT company FROM _share WHERE figi = ?";
        return jdbcTemplate.queryForObject(sql, String.class, figi);
    }

    public String getTickerByFigi(String figi) {
        String sql = "SELECT ticker FROM _share WHERE figi = ?";
        return jdbcTemplate.queryForObject(sql, String.class, figi);

    }

    public List<Share> getAllShare() {
        final String sql = "SELECT * FROM _share";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    return new Share(
                            rs.getInt("id"),
                            rs.getString("figi"),
                            rs.getString("company"),
                            rs.getString("ticker")
                    );
                });
    }

    public List<String> getAllFigis() {
        final String sql = "Select figi FROM _share";
        try {
            return jdbcTemplate.queryForList(sql, String.class);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    public Optional<String> importCsvToFigisTable() {
        final String sqlAddCompany = "INSERT INTO _share (figi, company, ticker) VALUES(?, ?, ?)";
        final String sqlSelectFigi = "SELECT figi FROM _share WHERE figi = ? or company = ? or ticker = ?";
        final String fileName = "figi.csv";

        ClassPathResource resource = new ClassPathResource(fileName);
        try (CSVReader csvReader = new CSVReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
        ) {
            String[] line;

            while ((line = csvReader.readNext()) != null) {
                if (line.length < 3) {
                    log.error("Некорректная строка в csv: {}", String.join(",", line));
                    return Optional.empty();
                }

                String ticker = line[2].trim();
                String figi = line[1].trim();
                String company = line[0].trim();

                try {
                    String figiFound = jdbcTemplate.queryForObject(
                            sqlSelectFigi,
                            String.class,
                            figi, company, ticker);
                    log.info("Компания уже существует: {}, {}, {}", company, figi, ticker);
                } catch (EmptyResultDataAccessException e) {
                    log.info("Компания добавлена: {}, {}, {}", company, figi, ticker);
                    jdbcTemplate.update(sqlAddCompany, figi, company, ticker);
                }
            }
            log.info("Данные из CSV успешно импортированы в таблицу _share.");
        } catch (IOException | CsvValidationException e) {
            log.error("Ошибка при чтении CSV-файла: {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.of("Данные успешно загружены");
    }

}
