package com.life_insurance_system.strategy;

import com.life_insurance_system.model.Application;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("UniversalLifePremium")
public class UniversalLifePremiumStrategy implements PremiumCalculationStrategy {

    private static final BigDecimal UNIVERSAL_LIFE_RATE = new BigDecimal("0.025"); // 2.5%

    @Override
    public BigDecimal calculatePremium(Application application) {
        if (application == null || application.getDesiredCoverage() == null) {
            return BigDecimal.ZERO;
        }
        return application.getDesiredCoverage().multiply(UNIVERSAL_LIFE_RATE);
    }
}
