package ru.viktorgezz.definition_of_anomaly.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.viktorgezz.definition_of_anomaly.company.intf.CompanyApiClient;
import ru.viktorgezz.definition_of_anomaly.util.ResponseExtractorUtils;

import java.util.Optional;

@Component
public class CompanyApiClientImpl implements CompanyApiClient {

    private final String GUIDE_SERVICE_URL;

    private final RestTemplate rT;

    @Autowired
    public CompanyApiClientImpl(
            @Value("${service.guide.url}") String guideServiceUrl,
            RestTemplate rT
    ) {
        GUIDE_SERVICE_URL = guideServiceUrl;
        this.rT = rT;
    }

    public Optional<CompanyRsDto> fetchCompanyByFigi(String figi) {
        try {
            return Optional.ofNullable(ResponseExtractorUtils.extractResponseBodyOrThrow(rT
                            .exchange(
                                    String.format("%s/company/%s", GUIDE_SERVICE_URL, figi),
                                    HttpMethod.GET,
                                    null,
                                    new ParameterizedTypeReference<CompanyRsDto>() {
                                    }
                            )
                    )
            );
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

}
