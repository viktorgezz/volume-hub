package ru.viktorgezz.company_info.company;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.company_info.company.intf.CompanyRepo;
import ru.viktorgezz.company_info.company.intf.CompanyService;
import ru.viktorgezz.company_info.exception.BusinessException;
import ru.viktorgezz.company_info.exception.ErrorCode;
import ru.viktorgezz.company_info.rabbitmq.CompanyMessage;
import ru.viktorgezz.company_info.rabbitmq.ProducerUpdMess;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepo companyRepo;
    private final ProducerUpdMess producerUpdMess;

    @Override
    public List<String> getAllFigis() {
        return StreamSupport
                .stream(companyRepo.findAll().spliterator(), false)
                .map(Company::getFigi)
                .collect(Collectors.toList());
    }

    @Override
    public List<Company> getAllCompany() {
        return StreamSupport
                .stream(companyRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public String findNameByFigi(String figi) {
        return Optional.ofNullable(companyRepo.getNameByFigi(figi))
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.COMPANY_NOT_FOUND)
                );
    }

    @Override
    public Company findCompanyByFigi(String figi) {
        return companyRepo.getCompanyByFigi(figi);
    }

    @Override
    public Company findCompanyByTicker(String ticker) {
        return companyRepo.getCompanyByTicker(ticker);
    }

    @Transactional
    @Override
    public void addCompany(Company company) {
        companyRepo.save(company);
    }

    @Transactional
    @Override
    public void deleteCompanyByTicker(String ticker) {
        int deletedRows = companyRepo.deleteByTicker(ticker);
        if (deletedRows == 0) {
            throw new BusinessException(ErrorCode.COMPANY_NOT_FOUND, ticker);
        }
    }

    @Transactional
    @Override
    public void updateFigiByTicker(String figi, String ticker) {
        int updatedRows = companyRepo.updateFigiByTicker(figi, ticker);
        if (updatedRows == 0) {
            throw new BusinessException(ErrorCode.COMPANY_NOT_FOUND, ticker);
        }
        producerUpdMess.sendMessage(CompanyMessage.builder().lookupTicker(ticker).figi(figi).build());
    }

    @Transactional
    @Override
    public void updateNameByTicker(String name, String ticker) {
        int updatedRows = companyRepo.updateNameByTicker(name, ticker);
        if (updatedRows == 0) {
            throw new BusinessException(ErrorCode.COMPANY_NOT_FOUND, ticker);
        }
        producerUpdMess.sendMessage(CompanyMessage.builder().lookupTicker(ticker).name(name).build());
    }

    @Transactional
    @Override
    public void updateTickerByTicker(String oldTicker, String newTicker) {
        int updatedRows = companyRepo.updateTickerByTicker(oldTicker, newTicker);
        if (updatedRows == 0) {
            throw new BusinessException(ErrorCode.COMPANY_NOT_FOUND, oldTicker);
        }
        producerUpdMess.sendMessage(CompanyMessage.builder().lookupTicker(oldTicker).tickerUpdate(newTicker).build());
    }
}
