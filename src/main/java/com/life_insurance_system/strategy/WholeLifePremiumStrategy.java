package com.life_insurance_system.strategy;

import com.life_insurance_system.model.Application;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("WholeLifePremium")
public class WholeLifePremiumStrategy implements PremiumCalculationStrategy {

    private static final BigDecimal WHOLE_LIFE_RATE = new BigDecimal("0.03"); // 3.0%

    @Override
    public BigDecimal calculatePremium(Application application) {
        if (application == null || application.getDesiredCoverage() == null) {
            return BigDecimal.ZERO;
        }
        return application.getDesiredCoverage().multiply(WHOLE_LIFE_RATE);
    }
}