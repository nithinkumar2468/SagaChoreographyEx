package org.saga.example.restaurant.controller;

import org.saga.example.restaurant.exceptions.HotelNotFoundException;
import org.saga.example.restaurant.model.Hotel;
import org.saga.example.restaurant.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HotelController {

    @Autowired
    private HotelRepository repo;

    @PostMapping("/save/hotel")
    public Hotel saveHotel(@RequestBody Hotel hotel){
        return repo.save(hotel);
    }

    @GetMapping("/get/hotel/{id}")
    public Hotel getHotelById(@PathVariable Integer id){
        return repo.findById(id).orElseThrow(
                ()->new HotelNotFoundException("Hotel with id : "+id+" not found.")
        );
    }
}
