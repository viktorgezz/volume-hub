package ru.viktorgezz.api_T_connector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.viktorgezz.api_T_connector.model.Share;
import ru.viktorgezz.api_T_connector.service.interf.ShareService;

import java.util.List;

@RestController
public class CompanyController {

    private final ShareService shareService;

    @Autowired
    public CompanyController(ShareService shareService) {
        this.shareService = shareService;
    }

    @GetMapping("/company-name/{figi}")
    public String sendCompanyNameByFigi(
            @PathVariable("figi") String figi
    ) {
        return shareService.getCompanyNameByFigi(figi);
    }

    @GetMapping("company-ticker/{figi}")
    public String sendTickerByFigi(
            @PathVariable("figi") String figi
    ) {
        return shareService.getTickerByFigi(figi);
    }

    @GetMapping("/share")
    public List<Share> sendShare() {
        return shareService.getAllShare();
    }

    @GetMapping("/figi")
    public List<String> sendFigis() {
        return shareService.getAllFigis();
    }
}
