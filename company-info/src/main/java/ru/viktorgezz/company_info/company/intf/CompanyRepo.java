package ru.viktorgezz.company_info.company.intf;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.viktorgezz.company_info.company.Company;

@Repository
public interface CompanyRepo extends CrudRepository<Company, Long> {

    @Query("SELECT name " +
            "FROM moex_companies " +
            "WHERE figi = :figi")
    String getNameByFigi(String figi);

    @Query("SELECT * " +
            "FROM moex_companies " +
            "WHERE figi = :figi")
    Company getCompanyByFigi(String figi);

    @Query("SELECT * " +
            "FROM moex_companies " +
            "WHERE ticker = :ticker")
    Company getCompanyByTicker(String ticker);

    @Modifying
    @Query("DELETE FROM moex_companies " +
            "WHERE ticker = :ticker")
    int deleteByTicker(String ticker);

    @Modifying
    @Query("UPDATE moex_companies " +
            "SET figi = :figi " +
            "WHERE ticker = :ticker")
    int updateFigiByTicker(String figi, String  ticker);

    @Modifying
    @Query("UPDATE moex_companies " +
            "SET name = :name " +
            "WHERE ticker = :ticker")
    int updateNameByTicker(String name, String ticker);

    @Modifying
    @Query("UPDATE moex_companies " +
            "SET ticker = :newTicker " +
            "WHERE ticker = :oldTicker")
    int updateTickerByTicker(String oldTicker, String newTicker);

}
