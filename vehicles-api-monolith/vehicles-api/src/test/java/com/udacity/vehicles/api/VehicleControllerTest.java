package com.udacity.vehicles.api;

import com.udacity.vehicles.entity.*;
import com.udacity.vehicles.service.VehicleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class VehicleControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Vehicle> json;

    @Autowired
    private JacksonTester<List<Vehicle>> json2;

    @MockBean
    private VehicleService vehicleService;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Vehicle vehicle = getCar();
        vehicle.setId(1L);
        given(vehicleService.save(any())).willReturn(vehicle);
        given(vehicleService.findById(any())).willReturn(vehicle);
        given(vehicleService.list()).willReturn(Collections.singletonList(vehicle));

    }

    /**
     * Tests for successful creation of new car in the system
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Vehicle vehicle = getCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(vehicle).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        Vehicle vehicle = getCar();
        vehicle.setId(1L);
        List<Vehicle> vehicleList = new ArrayList();
        vehicleList.add(vehicle);

        mvc.perform(
                get("/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(json2.write(vehicleList).getJson()));

        verify(vehicleService, times(1)).list();

    }

    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        Vehicle vehicle = getCar();
        vehicle.setId(1L);

        mvc.perform(
                get("/cars/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json;charset=UTF-8")))
                .andExpect(content().json(json.write(vehicle).getJson()));
        verify(vehicleService, times(1)).findById(1L);
    }

    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        Vehicle vehicle = getCar();
        vehicle.setId(1L);
        mvc.perform(delete("/cars/1"))
                .andExpect(status().isNoContent());
        verify(vehicleService, times(1)).delete(1l);
    }

    @Test
    public void updateCar() throws Exception {
        Vehicle vehicle = getCar();
        vehicle.setId(1L);
        mvc.perform(
                put(new URI("/cars/1"))
                        .content(json.write(vehicle).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().json(json.write(vehicle).getJson()));
    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Vehicle getCar() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLocation(new Location(40.730610, -73.935242));
        VehicleDetails vehicleDetails = new VehicleDetails();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        vehicleDetails.setManufacturer(manufacturer);
        vehicleDetails.setModel("Impala");
        vehicleDetails.setMileage(32280);
        vehicleDetails.setExternalColor("white");
        vehicleDetails.setBody("sedan");
        vehicleDetails.setEngine("3.6L V6");
        vehicleDetails.setFuelType("Gasoline");
        vehicleDetails.setModelYear(2018);
        vehicleDetails.setProductionYear(2018);
        vehicleDetails.setNumberOfDoors(4);
        vehicle.setVehicleDetails(vehicleDetails);
        vehicle.setVehicleCondition(VehicleCondition.USED);
        return vehicle;
    }
}