package org.saga.example.restaurant.controller;

import org.saga.example.restaurant.exceptions.HotelNotFoundException;
import org.saga.example.restaurant.model.Hotel;
import org.saga.example.restaurant.repository.HotelRepository;
import org.saga.example.shared.DTO.LoginFront;
import org.saga.example.shared.DTO.LoginMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin({"http://localhost:3000", "http://localhost:3001"})
public class HotelController {

    @Autowired
    private HotelRepository repo;

    @PostMapping("/save/hotel")
    public Hotel saveHotel(@RequestBody Hotel hotel) {
        return repo.save(hotel);
    }

    @PostMapping(path = "/vendor/login")
    LoginMessage loginuser(@RequestBody LoginFront loginFront) {
        Hotel hotelId = repo.findById(Integer.valueOf(loginFront.getEmail())).get();
        if (hotelId != null) {
            String password = loginFront.getPassword();
            String userpass = hotelId.getPassword();
            if (password.matches(userpass)) {
                return new LoginMessage("Login Success", true);
            } else {
                return new LoginMessage("Incorrect emailId or Password", false);
            }
        } else {
            return new LoginMessage("emailId not exist", false);
        }
    }

    @GetMapping("/get/hotel/{id}")
    public Hotel getHotelById(@PathVariable Integer id) {
        return repo.findById(id).orElseThrow(
                () -> new HotelNotFoundException("Hotel with id : " + id + " not found.")
        );
    }

    /*@GetMapping("getallhotelswithproducts/{id}")
    public List<Products> getallHotelProducts(@PathVariable Integer id) {
        HotelWithProducts data = new HotelWithProducts();
        Hotel hotel = repo.findById(id).orElseThrow(
                () -> new HotelNotFoundException("Hotel With " + id + " not Found.")
        );
       *//* data.setHotel(hotel);
        data.setProducts();*//*
        List<Products> products = service.getallProductsById(id);
        return products;
    }*/

    @GetMapping("/getall/hotels")
    public List<Hotel> getAllHotels() {
        return repo.findAll();
    }
}
