package org.saga.example.restaurant.controller;

import org.modelmapper.ModelMapper;
import org.saga.example.restaurant.model.Restaurant;
import org.saga.example.restaurant.repository.RestaurantRepository;
import org.saga.example.restaurant.service.RestaurantService;
import org.saga.example.shared.restaurant.RestaurantDTO;
import org.saga.example.shared.restaurant.UpdateOrderRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:3001")
public class RestaurantController {

    private final RestaurantRepository repo;
    private final ModelMapper modelMapper;
    private final RestaurantService service;

    public RestaurantController(RestaurantRepository repo, ModelMapper modelMapper,RestaurantService service) {
        this.repo = repo;
        this.modelMapper = modelMapper;
        this.service=service;
    }

    @GetMapping("/getall/{hotelId}")
    public List<RestaurantDTO> getAllOrders(@PathVariable Integer hotelId) {
        List<Restaurant> orders = repo.findByhotelId(hotelId);
        return orders.stream().map(order -> modelMapper.map(order, RestaurantDTO.class)).collect(Collectors.toList());
    }
    @GetMapping("/get/{orderId}")
    public Restaurant getOrderById(@PathVariable UUID orderId){
        return service.getOrderById(orderId);
    }

    @PutMapping("/update/{orderId}")
    public RestaurantDTO updateOrder(@RequestBody UpdateOrderRequest restaurant, @PathVariable UUID orderId) throws InterruptedException {
        return service.updateOrderByOrderId(restaurant,orderId);
    }
}
