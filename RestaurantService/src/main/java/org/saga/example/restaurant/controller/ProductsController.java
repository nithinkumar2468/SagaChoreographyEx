package org.saga.example.restaurant.controller;

import org.saga.example.restaurant.model.Hotel;
import org.saga.example.restaurant.model.Products;
import org.saga.example.restaurant.model.ProductsDTO;
import org.saga.example.restaurant.repository.HotelRepository;
import org.saga.example.restaurant.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductsController {

    @Autowired
    private ProductsRepository repo;

    @Autowired
    private HotelRepository hotelRepo;

    @PostMapping("/save/products/{hotelId}")
    public Products saveProducts(@RequestBody ProductsDTO dto, @PathVariable Integer hotelId){
        Hotel hotel=hotelRepo.findById(hotelId).get();
        Products product=new Products();
        product.setItem(dto.getItem());
        product.setQuantity(dto.getQuantity());
        product.setHotel(hotel);

        return repo.save(product);
    }
}
