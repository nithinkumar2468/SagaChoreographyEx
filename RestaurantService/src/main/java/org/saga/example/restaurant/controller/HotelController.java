package org.saga.example.restaurant.controller;

import org.saga.example.restaurant.model.Hotel;
import org.saga.example.restaurant.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HotelController {

    @Autowired
    private HotelRepository repo;

    @PostMapping("/save/hotel")
    public Hotel saveHotel(@RequestBody Hotel hotel){
        return repo.save(hotel);
    }
}
