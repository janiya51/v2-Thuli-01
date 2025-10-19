package com.life_insurance_system.service;

import com.life_insurance_system.model.Application;
import com.life_insurance_system.strategy.PremiumCalculationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PremiumService {

    private final Map<String, PremiumCalculationStrategy> strategyMap;

    @Autowired
    public PremiumService(Map<String, PremiumCalculationStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public BigDecimal calculatePremium(Application application) {
        String strategyName = getStrategyName(application.getProductType());
        PremiumCalculationStrategy strategy = strategyMap.get(strategyName);

        if (strategy == null) {
            throw new IllegalArgumentException("No premium calculation strategy found for product type: " + application.getProductType());
        }

        return strategy.calculatePremium(application);
    }

    private String getStrategyName(String productType) {
        if (productType == null) {
            return null;
        }
        if (productType.toLowerCase().contains("term")) {
            return "TermLifePremium";
        } else if (productType.toLowerCase().contains("whole")) {
            return "WholeLifePremium";
        }
        // Add more mappings as needed
        return null;
    }
}