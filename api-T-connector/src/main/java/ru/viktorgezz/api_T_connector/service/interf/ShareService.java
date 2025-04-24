package ru.viktorgezz.api_T_connector.service.interf;

import ru.viktorgezz.api_T_connector.model.Share;

import java.util.List;
import java.util.Optional;

public interface ShareService {

    String getCompanyNameByFigi(String figi);

    String getTickerByFigi(String figi);

    List<Share> getAllShare();

    List<String> getAllFigis();

    Optional<String> importCsvToFigisTable();
}
