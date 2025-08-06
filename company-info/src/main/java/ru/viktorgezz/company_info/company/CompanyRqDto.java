package ru.viktorgezz.company_info.company;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CompanyRqDto {

    @NotNull
    @Size(max = 300)
    private final String name;

    @NotNull
    @Size(max = 8)
    private final String ticker;

    @NotNull
    @Size(max = 15)
    private final String figi;
}
