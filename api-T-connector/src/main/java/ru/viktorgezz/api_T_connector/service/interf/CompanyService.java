package ru.viktorgezz.api_T_connector.service.interf;

import ru.viktorgezz.api_T_connector.model.Share;

import java.util.List;

public interface CompanyService {

    String getCompanyNameByFigi(String figi);

    String getTickerByFigi(String figi);

    List<Share> getAllShare();

    List<String> getAllFigis();
}
