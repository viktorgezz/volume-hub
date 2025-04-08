package ru.viktorgezz.api_T_connector.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.core.InvestApi;

@Component
public class ConnectTApiInvest {

    private final InvestApi investApi;

    public ConnectTApiInvest(@Value("${token.invest-t}") String token) {
        this.investApi = InvestApi.create(token);
    }

    public InvestApi getInvestApi() {
        return investApi;
    }
}
