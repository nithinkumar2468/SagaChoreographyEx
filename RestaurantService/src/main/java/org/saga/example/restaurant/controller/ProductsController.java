package org.saga.example.restaurant.controller;

import org.modelmapper.ModelMapper;
import org.saga.example.restaurant.model.Hotel;
import org.saga.example.restaurant.exceptions.ProductNotFoundException;
import org.saga.example.restaurant.model.Products;
import org.saga.example.restaurant.model.ProductsDTO;
import org.saga.example.restaurant.repository.HotelRepository;
import org.saga.example.restaurant.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:3000")
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

    @GetMapping("/getall/products")
    public List<Products> getAllProducts(){
        List<Products> products=repo.findAll();
        products.stream().map(product -> mapper.map(product, ProductsDTO.class)).collect(Collectors.toList());
        return repo.findAll();
    }

    @GetMapping("/get/product/{id}")
    public Products getProductById(@PathVariable Integer id){

        Products product=repo.findById(id).orElseThrow(
                ()->new ProductNotFoundException("Product with id : "+id+" not found.")
        );
        return repo.findById(id).orElseThrow(
                ()->new ProductNotFoundException("Product with id : "+id+" not found.")
        );
    }
}
