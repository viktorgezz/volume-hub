package ru.viktorgezz.api_T_connector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.api_T_connector.model.Share;
import ru.viktorgezz.api_T_connector.service.interf.CompanyService;

import java.util.List;

@RestController
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/company-name/{figi}")
    public String sendCompanyNameByFigi(
            @PathVariable("figi") String figi
    ) {
        return companyService.getCompanyNameByFigi(figi);
    }

    @GetMapping("company-ticker/{figi}")
    public String sendTickerByFigi(
            @PathVariable("figi") String figi
    ) {
        return companyService.getTickerByFigi(figi);
    }

    @GetMapping("/share")
    public List<Share> sendShare() {
        return companyService.getAllShare();
    }

    @GetMapping("/figi")
    public List<String> sendFigis() {
        return companyService.getAllFigis();
    }
}
