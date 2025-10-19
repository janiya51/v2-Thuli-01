package com.life_insurance_system.service;

import com.life_insurance_system.model.Application;
import com.life_insurance_system.strategy.PremiumCalculationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PremiumService {

    // A map to hold all the premium calculation strategies, injected by Spring.
    private final Map<String, PremiumCalculationStrategy> strategyMap;

    /**
     * Constructs a PremiumService with a map of premium calculation strategies.
     * @param strategyMap A map of strategy names to their implementations.
     */
    @Autowired
    public PremiumService(Map<String, PremiumCalculationStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    /**
     * Calculates the premium for a given application using the appropriate strategy.
     * @param application The application for which to calculate the premium.
     * @return The calculated premium as a BigDecimal.
     * @throws IllegalArgumentException if no strategy is found for the application's product type.
     */
    public BigDecimal calculatePremium(Application application) {
        String strategyName = getStrategyName(application.getProductType());
        PremiumCalculationStrategy strategy = strategyMap.get(strategyName);

        if (strategy == null) {
            throw new IllegalArgumentException("No premium calculation strategy found for product type: " + application.getProductType());
        }

        return strategy.calculatePremium(application);
    }

    /**
     * Determines the name of the strategy to use based on the product type.
     * @param productType The product type from the application.
     * @return The name of the strategy to use, or null if no mapping is found.
     */
    private String getStrategyName(String productType) {
        if (productType == null) {
            return null;
        }
        if (productType.toLowerCase().contains("term")) {
            return "TermLifePremium";
        } else if (productType.toLowerCase().contains("whole")) {
            return "WholeLifePremium";
        } else if (productType.toLowerCase().contains("universal")) {
            return "UniversalLifePremium";
        }
        // Add more mappings as needed
        return null;
    }
}