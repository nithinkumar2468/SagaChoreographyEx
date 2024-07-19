package org.saga.example.order.service;

import org.saga.example.order.exceptions.HotelInactiveException;
import org.saga.example.order.model.Hotel;
import org.saga.example.order.model.OrderPurchase;
import org.saga.example.order.repository.HotelRepository;
import org.saga.example.order.repository.OrderPurchaseRepository;
import org.saga.example.shared.DTO.OrderPurchaseDTO;
import org.saga.example.shared.order.OrderQueue;
import org.saga.example.shared.order.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class OrderPurchaseService {

    private static final Logger log = LoggerFactory.getLogger(OrderPurchaseService.class);

    @Autowired
    private OrderPurchaseRepository repo;

    @Autowired
    private HotelRepository hrrepo;

    @Autowired
    private OrderPublisher publisher;

    @Transactional(rollbackFor = {HotelInactiveException.class}, propagation = Propagation.REQUIRED)
    public OrderPurchase createOrderPurchase(OrderPurchaseDTO purchase) {
        OrderPurchase orderpurchase = new OrderPurchase();
        orderpurchase.setOrderId(purchase.getOrderId());
        orderpurchase.setCustomerId(purchase.getCustomerId());
        orderpurchase.setProductId(purchase.getProductId());
        orderpurchase.setPrice(purchase.getPrice());
        orderpurchase.setHotelId(purchase.getHotelId());
        orderpurchase.setQuantity(purchase.getQuantity());
        orderpurchase.setOrderStatus(String.valueOf(OrderStatus.ORDER_CREATED));
        orderpurchase.setPaymentMethod(purchase.getPaymentMethod());
        String time = new Timestamp(System.currentTimeMillis()).toString();
        orderpurchase.setCreatedTimeStamp(time);

        repo.save(orderpurchase);

        log.info("Order Created for orderId : " + purchase.getOrderId());

        publisher.publish(orderpurchase);

        Hotel status = hrrepo.findById(purchase.getHotelId()).get();
        if (status.getStatus().equalsIgnoreCase("inactive")) {
            throw new RuntimeException("Hotel InActive For orderId : " + purchase.getOrderId());
        }
        return orderpurchase;
    }

    public List<OrderPurchase> getAllOrders() {
        return repo.findAll();
    }

    public OrderPurchase getByID(@PathVariable UUID orderId) {
        return repo.findById(orderId).get();
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
}
