package ru.viktorgezz.company_info.company.intf;

import ru.viktorgezz.company_info.CompanyRsDto;
import ru.viktorgezz.company_info.company.Company;
import ru.viktorgezz.company_info.company.CompanyRqDto;

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
}
