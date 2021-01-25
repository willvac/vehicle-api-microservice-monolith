package com.udacity.vehicles.service;


import com.udacity.vehicles.entity.Price;
import com.udacity.vehicles.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements the pricing service to get prices for each vehicle.
 */
@Service
public class PriceService {

    @Autowired
    PriceRepository priceRepository;

    /**
     * Gets a random price to fill in for a given vehicle ID.
     * @return random price for a vehicle
     */
    private static BigDecimal randomPrice() {
        return new BigDecimal(ThreadLocalRandom.current().nextDouble(1, 5))
                .multiply(new BigDecimal(5000d)).setScale(2, RoundingMode.HALF_UP);
    }

    public Price findByVehicleId(Long vehicleId) {
        Price price = priceRepository.findById(vehicleId).orElse(null);
        return price;
    }

    public Price generatePriceForVehicle(Long vehicleId) {
        Price price = new Price("USD", randomPrice(), vehicleId);
        price.setVehicleId(vehicleId);
        savePrice(price);
        return price;
    }

    public void deleteByVehicleId(Long vehicleId) {
        priceRepository.deleteById(vehicleId);
    }

    public Price savePrice(Price price) {
        return priceRepository.save(price);
    }

}
