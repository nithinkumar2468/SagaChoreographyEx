package org.saga.example.order.controller;

import io.reactivex.rxjava3.core.Observable;
import org.modelmapper.ModelMapper;
import org.saga.example.order.model.OrderPurchase;
import org.saga.example.order.service.OrderPurchaseService;
import org.saga.example.shared.DTO.OrderPurchaseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class OrderPurchaseController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OrderPurchaseService service;

    @PostMapping("/create")
    Observable<OrderPurchase> createOrder(@RequestBody OrderPurchaseDTO purchase) {
        purchase.setOrderId(UUID.randomUUID());
        return Observable.fromCallable(() -> service.createOrderPurchase(purchase));
    }

    @GetMapping("/getall")
    List<OrderPurchase> getOrders() {
        List<OrderPurchase> dtoList = new ArrayList<>();
        List<OrderPurchase> orders = service.getAllOrders();
        Observable.fromIterable(orders)
                .flatMap(products2 -> Observable.just(products2))
                .doOnNext(t -> {
                    OrderPurchase to = modelMapper.map(t, OrderPurchase.class);
                    dtoList.add(to);
                })
                .toList().blockingSubscribe();
        return dtoList;
    }

}
