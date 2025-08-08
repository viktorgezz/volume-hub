package ru.viktorgezz.definition_of_anomaly.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.viktorgezz.definition_of_anomaly.util.ResponseExtractorUtils;

@Component
public class CompanyClientImpl implements CompanyClient {

    private final String GUIDE_SERVICE_URL;

    private final RestTemplate rT;

    @Autowired
    public CompanyClientImpl(
            @Value("${service.guide.url}") String guideServiceUrl,
            RestTemplate rT
    ) {
        GUIDE_SERVICE_URL = guideServiceUrl;
        this.rT = rT;
    }

    public CompanyRsDto fetchCompanyByFigi(String figi) {
        return ResponseExtractorUtils.extractResponseBodyOrThrow(rT
                .exchange(
                        String.format("%s/company/%s", GUIDE_SERVICE_URL, figi),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<CompanyRsDto>() {
                        }
                )
        );
    }

}
