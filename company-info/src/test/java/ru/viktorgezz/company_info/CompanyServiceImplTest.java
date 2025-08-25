package ru.viktorgezz.company_info;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.viktorgezz.company_info.company.Company;
import ru.viktorgezz.company_info.company.intf.CompanyRepo;
import ru.viktorgezz.company_info.company.intf.CompanyService;
import ru.viktorgezz.company_info.exception.BusinessException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
public class CompanyServiceImplTest extends AbstractIntegrationBDTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepo companyRepo;

    private static Company company;

    @BeforeEach
    void setUp() {
        companyRepo.deleteAll();
        company.setId(null);
    }

    @BeforeAll
    static void setUpp() {
        company = Company
                .builder()
                .name("Газпром")
                .figi("BBG004730RP0")
                .ticker("GAZP")
                .build();
    }

    @Test
    void addCompany_shouldSaveCompany() {

        companyService.addCompany(company);

        assertThat(companyService
                .findNameByFigi(company.getFigi()))
                .isEqualTo(company.getName());
    }

    @Test
    void findCompanyByTicker_MustBeFoundCompany() {
        companyService.addCompany(company);

        assertThat(companyService
                .findCompanyByFigi(company.getFigi()))
                .isInstanceOf(Company.class);
    }

    @Test
    void deleteCompanyByTicker_existingCompany_throwsBusinessException() {
        companyService.addCompany(company);
        companyService.deleteCompanyByTicker(company.getTicker());

        assertThatThrownBy(() -> companyService.findNameByFigi(company.getFigi()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void deleteCompanyByTicker_nonexistentCompany_throwsBusinessException() {
        assertThatThrownBy(() -> companyService.deleteCompanyByTicker(company.getTicker()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updateFigiByTicker_existingCompany_successUpdate() {
        String newFigi = "GAZZZZZZZZZZZZZ";

        companyService.addCompany(company);
        companyService.updateFigiByTicker(newFigi, company.getTicker());

        assertThat(companyService.findCompanyByTicker(company.getTicker()).getFigi()).isEqualTo(newFigi);
    }


    @Test
    void updateNameByTicker_existingCompany_successUpdate() {
        String newName = "Gezprom";

        companyService.addCompany(company);
        companyService.updateNameByTicker(newName, company.getTicker());

        assertThat(companyService.findCompanyByTicker(company.getTicker()).getName()).isEqualTo(newName);
    }


    @Test
    void updateTickerByTicker_existingCompany_successUpdate() {
        String newTicker = "GAZZ";

        companyService.addCompany(company);
        companyService.updateTickerByTicker(newTicker, company.getTicker());

        assertThat(companyService.findCompanyByTicker(newTicker).getTicker()).isEqualTo(newTicker);
    }
}
