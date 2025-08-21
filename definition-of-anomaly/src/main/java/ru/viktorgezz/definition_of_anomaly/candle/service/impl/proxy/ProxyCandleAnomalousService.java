package ru.viktorgezz.definition_of_anomaly.candle.service.impl.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.viktorgezz.definition_of_anomaly.candle.model.CandleMessage;
import ru.viktorgezz.definition_of_anomaly.candle.service.impl.CandleAnomalousByIrvinServiceImpl;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleAnomalousService;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleService;
import ru.viktorgezz.definition_of_anomaly.company.CompanyService;

@Service
public class ProxyCandleAnomalousService implements CandleAnomalousService {

    private final CandleAnomalousByIrvinServiceImpl candleAnomalousByIrvinService;
    private final CandleService candleService;
    private final CompanyService companyService;

    @Autowired
    public ProxyCandleAnomalousService(
            CandleAnomalousByIrvinServiceImpl candleAnomalousByIrvinService,
            CandleService candleService,
            CompanyService companyService
    ) {
        this.candleAnomalousByIrvinService = candleAnomalousByIrvinService;
        this.candleService = candleService;
        this.companyService = companyService;
    }

    @Override
    public void foundAnomalyCandle(CandleMessage candle) {
        candleService.saveCandle(candle, companyService.getIdCompanyByFigi(candle.getFigi()));
        candleAnomalousByIrvinService.foundAnomalyCandle(candle);
    }
}
