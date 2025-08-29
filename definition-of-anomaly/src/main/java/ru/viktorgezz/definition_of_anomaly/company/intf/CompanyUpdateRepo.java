package ru.viktorgezz.definition_of_anomaly.company.intf;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.viktorgezz.definition_of_anomaly.company.Company;

@Repository
public interface CompanyUpdateRepo extends CrudRepository<Company, Long> {

    @Modifying
    @Query("UPDATE company " +
            "SET figi = :figi " +
            "WHERE ticker = :ticker")
    void updateFigiByTicker(String figi, String ticker);

    @Modifying
    @Query("UPDATE company " +
            "SET name = :name " +
            "WHERE ticker = :ticker")
    void updateNameByTicker(String name, String ticker);

    @Modifying
    @Query("UPDATE company " +
            "SET ticker = :newTicker " +
            "WHERE ticker = :oldTicker")
    void updateTickerByTicker(String newTicker, String oldTicker);
}
