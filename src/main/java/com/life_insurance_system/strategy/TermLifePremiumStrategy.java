package com.life_insurance_system.strategy;

import com.life_insurance_system.model.Application;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("TermLifePremium")
public class TermLifePremiumStrategy implements PremiumCalculationStrategy {

    private static final BigDecimal TERM_LIFE_RATE = new BigDecimal("0.015"); // 1.5%

    @Override
    public BigDecimal calculatePremium(Application application) {
        if (application == null || application.getDesiredCoverage() == null) {
            return BigDecimal.ZERO;
        }
        return application.getDesiredCoverage().multiply(TERM_LIFE_RATE);
    }
}