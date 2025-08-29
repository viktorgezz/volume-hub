package ru.viktorgezz.definition_of_anomaly.candle;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.viktorgezz.definition_of_anomaly.candle.model.CandleMessage;
import ru.viktorgezz.definition_of_anomaly.candle.service.intf.CandleService;

@Aspect
@Component
public class AnomalyCandleAspect {

    private static final Logger log = LoggerFactory.getLogger(AnomalyCandleAspect.class);
    private final CandleService candleService;

    @Autowired
    public AnomalyCandleAspect(CandleService candleService) {
        this.candleService = candleService;
    }

    @AfterReturning(
            pointcut = "execution(public * ru.viktorgezz.definition_of_anomaly.candle.service.impl.TempAnomalyServiceImpl.isAnomaly(..)) && args(candle, idCompany)",
            returning = "isAnomaly",
            argNames = "candle, idCompany, isAnomaly"
    )
    public void afterIsAnomaly(CandleMessage candle, long idCompany, boolean isAnomaly) {
        log.debug("Сохранение свечи, через аспект: {}, {}", idCompany, isAnomaly);
        candleService.saveCandle(candle, idCompany, isAnomaly);
    }
}
