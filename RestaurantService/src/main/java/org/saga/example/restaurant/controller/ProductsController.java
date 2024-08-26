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

import java.util.List;

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
    public Products saveProducts(@RequestBody ProductsDTO dto, @PathVariable Integer hotelId) {
        Hotel hotel = hotelRepo.findById(hotelId).get();
        Products product = new Products();
        product.setItem(dto.getItem());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setHotel(hotel);

        return repo.save(product);
    }

    @GetMapping("/products")
    public List<Products> getAllProducts() {
        //List<Products> products=repo.findAll();
        //products.stream().map(product -> mapper.map(product, ProductsDTO.class)).collect(Collectors.toList());
        return repo.findAll();
    }

    @GetMapping("/product/{id}")
    public Products getProductById(@PathVariable Integer id) {

        /*Products product=repo.findById(id).orElseThrow(
                ()->new ProductNotFoundException("Product with id : "+id+" not found.")
        );*/
        return repo.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id : " + id + " not found."));
    }

    @GetMapping("/get/products/{hotelId}")
    public List<Products> getProductsByHotelId(@PathVariable Integer hotelId) {
        return repo.findByHotelHotelId(hotelId);
    }

    @GetMapping("/productsbycategory/{category}")
    public List<Products> getProductsByCategory(@PathVariable String category){
        return repo.findByCategory(category);
    }
}