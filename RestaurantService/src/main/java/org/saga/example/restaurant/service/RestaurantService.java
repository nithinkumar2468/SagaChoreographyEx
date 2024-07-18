package org.saga.example.restaurant.service;

import org.saga.example.orders.order.OrderQueue;
import org.saga.example.orders.order.OrderState;
import org.saga.example.orders.restaurant.PaymentResponseFromRestaurant;
import org.saga.payment.entity.Payment;
import org.saga.example.restaurant.model.Hotel;
import org.saga.example.restaurant.model.Products;
import org.saga.example.restaurant.model.Restaurant;
import org.saga.example.restaurant.repository.HotelRepository;
import org.saga.example.restaurant.repository.ProductsRepository;
import org.saga.example.restaurant.repository.RestaurantRepository;
import org.saga.example.restaurant.sqs.RestaurantPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository repo;
    @Autowired
    private HotelRepository hrepo;
    @Autowired
    private ProductsRepository prodRepo;
    @Autowired
    private RestaurantPublisher publisher;

    public void send(Payment payment) throws InterruptedException {

        Hotel hotel = hrepo.findById(payment.getHotelId()).get();
        if (hotel.getStatus().equalsIgnoreCase("inactive")) {
            
            OrderQueue response = OrderQueue.of(payment.getOrderId(), OrderState.ORDER_FAILED, "inactive");
            publisher.publishToOrder(response);

            PaymentResponseFromRestaurant res = PaymentResponseFromRestaurant.of(payment.getOrderId(), payment.getPaymentId(), payment.getAmount(), OrderState.ORDER_FAILED,"inactive");
            publisher.publish(res);
        } else {
            Products product = prodRepo.findById(payment.getProductId()).get();

            Restaurant restaurant = new Restaurant();
            restaurant.setOrderId(payment.getOrderId());
            restaurant.setHotelId(hotel.getHotelId());
            restaurant.setHotelName(hotel.getHotelName());
            restaurant.setQuantity(payment.getQuantity());
            restaurant.setPrice(payment.getAmount());
            restaurant.setItem(product.getItem());
            restaurant.setUserId(payment.getCustomerId());

            if (product.getQuantity() >= payment.getQuantity()) {

                restaurant.setRestaurantOrderStatus("success");
                repo.save(restaurant);

                publisher.publish(restaurant);

                product.setQuantity(product.getQuantity() - payment.getQuantity());
                prodRepo.save(product);

                OrderQueue response = OrderQueue.of(payment.getOrderId(), OrderState.ORDER_PREPARED, "success");
                publisher.publishToOrder(response);
            } else {
                restaurant.setRestaurantOrderStatus("failure");
                repo.save(restaurant);

                PaymentResponseFromRestaurant res = PaymentResponseFromRestaurant.of(payment.getOrderId(), payment.getPaymentId(), payment.getAmount(), OrderState.ORDER_FAILED,"failure");
                publisher.publish(res);

                OrderQueue response = OrderQueue.of(payment.getOrderId(), OrderState.ORDER_FAILED, "failure");
                publisher.publishToOrder(response);
            }
        }
    }

    public List<Restaurant> getAllRestaurant() {
        return repo.findAll();
    }
}
