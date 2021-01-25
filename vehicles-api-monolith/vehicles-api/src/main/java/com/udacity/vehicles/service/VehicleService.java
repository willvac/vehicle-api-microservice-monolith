package com.udacity.vehicles.service;

import com.udacity.vehicles.entity.Address;
import com.udacity.vehicles.entity.Location;
import com.udacity.vehicles.entity.Price;
import com.udacity.vehicles.entity.Vehicle;
import com.udacity.vehicles.repository.VehicleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class VehicleService {

    @Autowired
    AddressService addressService;
    @Autowired
    PriceService priceService;
    @Autowired
    VehicleRepository vehicleRepository;

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the VehicleRepository
     */
    public List<Vehicle> list() {
        List<Vehicle> vehicleList = vehicleRepository.findAll();
        for(Vehicle vehicle : vehicleList) {
            populatePriceAndLocationData(vehicle);
        }
        return vehicleList;
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the vehicle to gather information on
     * @return the requested vehicle's information, including location and price
     */
    public Vehicle findById(Long id) {

        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(VehicleNotFoundException::new);
        populatePriceAndLocationData(vehicle); //if null, won't continue onto here so do not need to worry about null pointer exception
        return vehicle;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param vehicle A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    @Transactional
    public Vehicle save(Vehicle vehicle) {
        //if car exists = has an id
        if(vehicle.getId() != null) {
            Vehicle vehicleToSave = vehicleRepository.getOne(vehicle.getId()); //throws EntityNotFoundException if not found
            if(vehicle.getPrice() != null && vehicle.getLocation() != null) //vehicle lat & lon can never be null
                savePriceAndLocationData(vehicle);//user provided a price. save the price and location data
            copyVehicleProperties(vehicle, vehicleToSave);
            return vehicleToSave;
        } else {
            Vehicle savedVehicle = vehicleRepository.save(vehicle);
            vehicle.setId(savedVehicle.getId());
            if(vehicle.getPrice() != null) { //if user has supplied a price then do not have to generate one. save the price
                    savePriceAndLocationData(vehicle);
            }
            populatePriceAndLocationData(vehicle); //populate price and location data; will generate price if user did not provide one
            return vehicle;
        }
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    @Transactional
    public void delete(Long id) {
        vehicleRepository.deleteById(id);
        removePriceAndLocationData(id);
    }

    private void populatePriceAndLocationData(Vehicle vehicle) {
            Price price = retrievePriceData(vehicle); //if unsuccessful, will return null. Should it throw an error?
            Address address = retrieveAddressData(vehicle);
            if(price != null) //if trouble with price service
                vehicle.setPrice(price.getCurrency() + " " + price.getPrice());
            Location location = vehicle.getLocation();
            if(address != null)
                BeanUtils.copyProperties(address, location);
    }

    private void removePriceAndLocationData(Long vehicleId) {
        priceService.deleteByVehicleId(vehicleId);
        addressService.deleteByVehicleId(vehicleId);
    }

    private void savePriceAndLocationData(Vehicle vehicle) {
        Price price = convertStringPriceToPrice(vehicle.getPrice());
        price.setVehicleId(vehicle.getId());
        priceService.savePrice(price);

        //save location data below

    }

    private Price retrievePriceData(Vehicle vehicle) {
        Long vehicleId = vehicle.getId();
        Price price = priceService.findByVehicleId(vehicleId);
        if(price == null)
            price = priceService.generatePriceForVehicle(vehicleId);
        return price;
    }

    private Address retrieveAddressData(Vehicle vehicle) {
        Double lat = vehicle.getLocation().getLat();
        Double lon = vehicle.getLocation().getLon();
        Address address = addressService.get(lat, lon);
        return address;
    }

    private Price convertStringPriceToPrice(String stringPrice) {
        try {
            String[] split = stringPrice.split(" ");//assumes price is formatted as 'CURRENCY AMOUNT'
            Price price = new Price(split[0], new BigDecimal(split[1]), null);
            return price;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println("Error converting input to price. Input: " + stringPrice);
            throw e;
        }
    }

    private void copyVehicleProperties(Vehicle source, Vehicle destination) {
        destination.setPrice(source.getPrice());
        destination.setLocation(source.getLocation());
        destination.setModifiedAt(LocalDateTime.now());
        destination.setVehicleCondition(source.getVehicleCondition());
        destination.setVehicleDetails(source.getVehicleDetails());
    }
}
