package org.saga.example.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.saga.example.order.exceptions.AwsSqsException;
import org.saga.example.order.exceptions.OrderNotFoundException;
import org.saga.example.order.model.OrderPurchase;
import org.saga.example.order.repository.OrderPurchaseRepository;
import org.saga.example.shared.DTO.OrderPurchaseDTO;
import org.saga.example.shared.order.OrderQueue;
import org.saga.example.shared.order.OrderState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;

@Service
public class OrderPurchaseService {

    private static final Logger log = LoggerFactory.getLogger(OrderPurchaseService.class);

    @Autowired
    private OrderPurchaseRepository repo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderPublisher publisher;

    @Transactional(rollbackFor = {AwsSqsException.class}, propagation = Propagation.REQUIRES_NEW)
    public OrderPurchase createOrderPurchase(OrderPurchaseDTO purchase) throws JsonProcessingException {
        OrderPurchase orderpurchase = new OrderPurchase();
        orderpurchase.setOrderId(UUID.randomUUID());
        orderpurchase.setCustomerId(purchase.getCustomerId());
        orderpurchase.setProductId(purchase.getProductId());
        orderpurchase.setPrice(purchase.getPrice());
        orderpurchase.setHotelId(purchase.getHotelId());
        orderpurchase.setQuantity(purchase.getQuantity());
        orderpurchase.setOrderStatus(String.valueOf(OrderState.ORDER_CREATED));
        orderpurchase.setPaymentMethod(purchase.getPaymentMethod());
        /*String time = new Timestamp(System.currentTimeMillis()).toString();
        orderpurchase.setCreatedTimeStamp(time);*/
        Instant instant = Instant.now().with(ChronoField.NANO_OF_SECOND, 123_456_789L);

        orderpurchase.setCreatedTimeStamp(instant);
        repo.save(orderpurchase);

        log.info("Order Created for orderId : " + orderpurchase.getOrderId());

        try {
            publisher.publish(orderpurchase);
            log.info("try triggered");
            return orderpurchase;
        }
        catch (Exception e) {
            log.info("catch triggered");
            throw new AwsSqsException("AWS Resource Not Found..!");
        }
        /*publisher.publish(orderpurchase);
        return orderpurchase;*/
    }

    public List<OrderPurchase> getAllOrders() {
        return repo.findAll();
    }

    public OrderPurchase getByID(@PathVariable UUID orderId) {
        return repo.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order with id : " + orderId + " not found.")
        );
    }

    public OrderQueue updateById(OrderQueue response) {

        /*repo.findById(response.orderId()).map(order->{
            order.orderStatus(String.valueOf(response.OrderState()));
            return repo.save(order);
        });*/
        repo.findById(response.getOrderId()).map(order -> {
            order.setOrderStatus(String.valueOf(response.getOrderState()));
            repo.save(order);
            return true;
        });
        log.info("Order-Service Update Triggered");
        return response;
    }

    public void deleteByorderId(UUID orderId) {
        repo.deleteById(orderId);
    }

    public List<OrderPurchase> GetAllOrdersByUserId(Integer userId) {
        List<OrderPurchase> orders = repo.findBycustomerId(userId);
        return orders;
    }
}
