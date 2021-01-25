package com.udacity.vehicles.service;

import com.udacity.vehicles.entity.Address;
import com.udacity.vehicles.repository.MockAddressRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    public Address get(Double lat, Double lon) {
        return MockAddressRepository.getRandom();
    }

    public void deleteByVehicleId(Long vehicleId) {
        //put code here
    }
}
