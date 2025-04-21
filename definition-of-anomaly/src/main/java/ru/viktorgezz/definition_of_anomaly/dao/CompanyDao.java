package ru.viktorgezz.definition_of_anomaly.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CompanyDao {

    private final static String NAME_TABLE_COMPANY = "company";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CompanyDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isCompanyPresent(String figi) {
        return getIdCompanyByFigi(figi) != 0;
    }

    public void save(String figi, String name) {
        final String sql = String.format("INSERT INTO %s (name, figi) values(?, ?);", NAME_TABLE_COMPANY);
        jdbcTemplate.update(
                sql,
                name, figi);
    }

    public long getIdCompanyByFigi(String figi) {
        final String sql = String.format("SELECT id FROM %s WHERE figi = ?;", NAME_TABLE_COMPANY);
        try {
            return Objects.requireNonNull(jdbcTemplate.queryForObject(
                    sql,
                    Long.class,
                    figi));
        } catch (NullPointerException | EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public String getNameCompanyByFigi(String figi) {
        final String sql = String.format("SELECT name FROM %s WHERE figi = ?;", NAME_TABLE_COMPANY);
        try {
            return Objects.requireNonNull(jdbcTemplate.queryForObject(
                            sql,
                            String.class,
                            figi
                    )
            );
        } catch (NullPointerException | EmptyResultDataAccessException e) {
            throw new RuntimeException("Ошибка поиска имени компании по figi");
        }
    }

    public List<Long> getIdsCompany() {
        final String sql = "Select id FROM company;";
        return jdbcTemplate.queryForList(sql, Long.class);
    }
}

