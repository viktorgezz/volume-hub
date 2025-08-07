package ru.viktorgezz.company_info.company.intf;

import ru.viktorgezz.company_info.company.CompanyRsDto;
import ru.viktorgezz.company_info.company.Company;
import ru.viktorgezz.company_info.company.CompanyRqDto;

import java.util.List;
import java.util.stream.Collectors;

public interface Converter {

    static Company convert(CompanyRqDto dto) {
        Company company = new Company();
        company.setFigi(dto.getFigi());
        company.setName(dto.getName());
        company.setTicker(dto.getTicker());
        return company;
    }

    static CompanyRsDto convert(Company company) {
        return new CompanyRsDto(
                company.getName(),
                company.getTicker(),
                company.getFigi()
        );
    }

    static List<CompanyRsDto> convert(List<Company> companies) {
        return companies
                .stream()
                .map(Converter::convert)
                .collect(Collectors.toList());
    }
}
