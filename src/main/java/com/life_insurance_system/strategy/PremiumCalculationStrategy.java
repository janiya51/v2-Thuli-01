package com.life_insurance_system.strategy;

import com.life_insurance_system.model.Application;
import java.math.BigDecimal;

public interface PremiumCalculationStrategy {
    BigDecimal calculatePremium(Application application);
}