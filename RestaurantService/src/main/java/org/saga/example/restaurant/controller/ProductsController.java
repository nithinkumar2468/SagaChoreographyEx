package org.saga.example.restaurant.controller;

import org.modelmapper.ModelMapper;
import org.saga.example.restaurant.exceptions.ProductNotFoundException;
import org.saga.example.restaurant.model.Hotel;
import org.saga.example.restaurant.model.Products;
import org.saga.example.restaurant.model.ProductsDTO;
import org.saga.example.restaurant.repository.HotelRepository;
import org.saga.example.restaurant.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductsController {

    @Autowired
    private ProductsRepository repo;

    @Autowired
    private HotelRepository hotelRepo;

    @Autowired
    private ModelMapper mapper;

    @PostMapping("/save/products/{hotelId}")
    public Products saveProducts(@RequestBody ProductsDTO dto, @PathVariable Integer hotelId){
        Hotel hotel=hotelRepo.findById(hotelId).get();
        Products product=new Products();
        product.setItem(dto.getItem());
        product.setQuantity(dto.getQuantity());
        product.setHotel(hotel);

        return repo.save(product);
    }

    @GetMapping("/get/product/{id}")
    public ProductsDTO getProductById(@PathVariable Integer id){

        Products product=repo.findById(id).orElseThrow(
                ()->new ProductNotFoundException("Product with id : "+id+" not found.")
        );
        return mapper.map(product,ProductsDTO.class);
    }
}
