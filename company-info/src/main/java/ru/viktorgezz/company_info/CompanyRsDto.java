package ru.viktorgezz.company_info;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompanyRsDto {

    @NotNull
    private final String name;

    @NotNull
    private final String ticker;

    @NotNull
    private final String figi;
}
