package org.saga.example.restaurant.service;

import org.modelmapper.ModelMapper;
import org.saga.example.payment.entity.Payment;
import org.saga.example.restaurant.exceptions.HotelNotFoundException;
import org.saga.example.restaurant.exceptions.OrderNotFoundException;
import org.saga.example.restaurant.model.Hotel;
import org.saga.example.shared.order.OrderQueue;
import org.saga.example.shared.order.OrderState;
import org.saga.example.shared.restaurant.PaymentResponseFromRestaurant;
import org.saga.example.shared.restaurant.RestaurantDTO;
import org.saga.example.shared.restaurant.UpdateOrderRequest;
import org.saga.example.restaurant.model.Products;
import org.saga.example.restaurant.model.Restaurant;
import org.saga.example.restaurant.repository.HotelRepository;
import org.saga.example.restaurant.repository.ProductsRepository;
import org.saga.example.restaurant.repository.RestaurantRepository;
import org.saga.example.restaurant.sqs.RestaurantPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private static final Logger log= LoggerFactory.getLogger(RestaurantService.class);

    @Autowired
    private HotelRepository hrepo;
    @Autowired
    private ProductsRepository prodRepo;
    @Autowired
    private RestaurantPublisher publisher;
    @Autowired
    private RestaurantRepository repo;
    @Autowired
    private RestaurantService service;
    @Autowired
    private ModelMapper modelMapper;

    /*private final RestaurantRepository repo;
    private final HotelRepository hrepo;
    private final ProductsRepository prodRepo;
    private final RestaurantPublisher publisher;
    private final ModelMapper modelMapper;

    public RestaurantService(RestaurantRepository repo, HotelRepository hrepo, ProductsRepository prodRepo, RestaurantPublisher publisher, ModelMapper modelMapper) {
        this.repo = repo;
        this.hrepo = hrepo;
        this.prodRepo = prodRepo;
        this.publisher = publisher;
        this.modelMapper = modelMapper;
    }*/

    public void send(Payment payment) throws InterruptedException {
        Hotel hotel = hrepo.findById(payment.getHotelId()).orElseThrow(
                ()->new HotelNotFoundException("Hotel with Id : "+payment.getHotelId()+" not found")
        );

        Products product = prodRepo.findById(payment.getProductId()).get();
        Restaurant restaurant = new Restaurant();
        restaurant.setOrderId(payment.getOrderId());
        restaurant.setHotelId(hotel.getHotelId());
        restaurant.setHotelName(hotel.getHotelName());
        restaurant.setQuantity(payment.getQuantity());
        restaurant.setPrice(payment.getAmount());
        restaurant.setItem(product.getItem());
        restaurant.setUserId(payment.getCustomerId());
        restaurant.setPaymentId(payment.getPaymentId());
        restaurant.setRestaurantOrderStatus("Pending");
        repo.save(restaurant);

        OrderQueue response = OrderQueue.of(payment.getOrderId(), OrderState.ORDER_PENDING, "success");
        publisher.publishToOrder(response);

    }

    public List<Restaurant> getAllRestaurant() {
        return repo.findAll();
    }

    public Restaurant getOrderById(@PathVariable UUID orderId){
        return repo.findById(orderId).orElseThrow(
                ()->new OrderNotFoundException("Order with Id : "+orderId+" Not Found")
        );
    }

    public RestaurantDTO updateOrderByOrderId(UpdateOrderRequest restaurant, UUID orderId) throws InterruptedException {
        Restaurant order = repo.findById(orderId).get();
        order.setRestaurantOrderStatus(restaurant.getRestaurantOrderStatus());
        if(restaurant.getRestaurantOrderStatus().equalsIgnoreCase("success")){

            OrderQueue response = OrderQueue.of(orderId, OrderState.ORDER_PREPARED, "success");
            publisher.publishToOrder(response);

            publisher.publishToDelivery(order);
        }
        else{
            OrderQueue response = OrderQueue.of(orderId, OrderState.ORDER_FAILED, "failure");
            publisher.publishToOrder(response);

            PaymentResponseFromRestaurant res = PaymentResponseFromRestaurant.of(orderId,order.getPaymentId(), order.getPrice(),OrderState.ORDER_FAILED,"failure");
            publisher.publishToPayment(res);
        }
        return modelMapper.map(repo.save(order), RestaurantDTO.class);
    }
}



 /*public void send(Payment payment) throws InterruptedException {

        Hotel hotel = hrepo.findById(payment.getHotelId()).orElseThrow(
                ()->new HotelNotFoundException("Hotel with Id : "+payment.getHotelId()+" not found")
        );
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

                restaurant.setRestaurantOrderStatus("Pending");
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
    }*/