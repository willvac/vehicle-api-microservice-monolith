package com.udacity.vehicles.controller;


import com.udacity.vehicles.entity.Vehicle;
import com.udacity.vehicles.service.VehicleService;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements a REST-based controller for the Vehicles API.
 */
@RestController
@RequestMapping("/cars")
class VehicleController {

    private final VehicleService vehicleService;
    private final CarResourceAssembler assembler;

    VehicleController(VehicleService vehicleService, CarResourceAssembler assembler) {
        this.vehicleService = vehicleService;
        this.assembler = assembler;
    }

    /**
     * Creates a list to store any vehicles.
     * @return list of vehicles
     */
    @GetMapping
    ResponseEntity<?> list() {
        List<Resource<Vehicle>> resources = vehicleService.list().stream().map(assembler::toResource)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    /**
     * Gets information of a specific car by ID.
     * @param id the id number of the given vehicle
     * @return all information for the requested vehicle
     */
    @GetMapping("/{id}")
    ResponseEntity<?> get(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.findById(id);
        Resource<Vehicle> resource = assembler.toResource(vehicle);
        return ResponseEntity.ok(resource.getId().expand().getHref());
    }

    /**
     * Posts information to create a new vehicle in the system.
     * @param vehicle A new vehicle to add to the system.
     * @return response that the new vehicle was added to the system
     * @throws URISyntaxException if the request contains invalid fields or syntax
     */
    @PostMapping
    ResponseEntity<?> post(@Valid @RequestBody Vehicle vehicle) throws URISyntaxException {
        Vehicle savedVehicle = vehicleService.save(vehicle);
        Resource<Vehicle> resource = assembler.toResource(savedVehicle);
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    /**
     * Updates the information of a vehicle in the system.
     * @param id The ID number for which to update vehicle information.
     * @param vehicle The updated information about the related vehicle.
     * @return response that the vehicle was updated in the system
     */
    @PutMapping("/{id}")
    ResponseEntity<?> put(@PathVariable Long id, @Valid @RequestBody Vehicle vehicle) {
        vehicle.setId(id);
        Vehicle savedVehicle = vehicleService.save(vehicle);
        Resource<Vehicle> resource = assembler.toResource(savedVehicle);
        return ResponseEntity.ok(resource.getId().expand().getHref());
    }

    /**
     * Removes a vehicle from the system.
     * @param id The ID number of the vehicle to remove.
     * @return response that the related vehicle is no longer in the system
     */
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
