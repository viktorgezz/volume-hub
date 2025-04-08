package ru.viktorgezz.api_T_connector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.api_T_connector.service.interf.CompanyService;

@RestController
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/company-name/{figi}")
    public String getCompanyNameByFigi(
            @PathVariable("figi") String title
    ) {
        return companyService.getCompanyNameByFigi(title);
    }
}
