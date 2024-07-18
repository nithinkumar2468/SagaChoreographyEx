package org.saga.example.delivery.service;

import org.saga.example.delivery.entity.Delivery;
import org.saga.example.delivery.repository.DeliveryRepository;
import org.saga.example.delivery.sqs.OrderEventPublisher;
import org.saga.example.orders.order.OrderQueue;
import org.saga.example.orders.order.OrderState;
import org.saga.restaurant.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository repo;

    @Autowired
    private OrderEventPublisher publisher;

    public void send(Restaurant request) throws InterruptedException {
        Delivery delivery = new Delivery();
        delivery.setDeliveryId(UUID.randomUUID());
        delivery.setOrderId(request.getOrderId());
        delivery.setCustomerId(request.getUserId());
        delivery.setHotel(request.getHotelName());
        delivery.setItem(request.getItem());
        delivery.setQuantity(request.getQuantity());
        String time = new Timestamp(System.currentTimeMillis()).toString();
        delivery.setDeliveryTime(time);
        delivery.setDeliveryStatus("Successful");

        repo.save(delivery);

        Thread.sleep(1000);
        OrderQueue response=OrderQueue.of(request.getOrderId(),OrderState.ORDER_DELIVERED,"success");
        publisher.publish(response);
    }

    public List<Delivery> getAllDeliveries(){
        return repo.findAll();
    }
}
