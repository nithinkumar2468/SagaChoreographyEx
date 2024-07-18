package org.saga.example.restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import org.saga.example.restaurant.service.RestaurantService;
import org.saga.example.restaurant.sqs.RestaurantPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = RestaurantServiceTests.class)
public class RestaurantServiceTests {

    private static final Logger log= LoggerFactory.getLogger(RestaurantServiceTests.class);

    @Mock
    private HotelRepository hrepo;
    @Mock
    private ProductsRepository prodRepo;
    @Mock
    private RestaurantPublisher publisher;
    @MockBean
    private RestaurantRepository repo;
    @InjectMocks
    private RestaurantService service;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void saveSuccessRestaurant() throws InterruptedException {
        UUID id= UUID.randomUUID();

        when(repo.findAll()).thenReturn(Stream.of(Restaurant.of(id,1001,"UDUPI",20,"dosa",2,200,"SUCCESS"),Restaurant.of(UUID.randomUUID(),1002,"Shadab",22,"Biryani",1,200,"SUCCESS")).collect(Collectors.toList()));

        Hotel hotel=new Hotel();
        hotel.setHotelId(1001);
        hotel.setHotelName("UDUPI");
        hotel.setStatus("active");

        when(hrepo.findById(1001)).thenReturn(Optional.of(hotel));

        Products product=new Products();
        product.setProductId(101);
        product.setItem("DOSA");
        product.setHotel(hotel);
        product.setQuantity(4);

        when(prodRepo.findById(101)).thenReturn(Optional.of(product));

        service.send(Payment.of(UUID.randomUUID(), id, 20, 101, 1001, 2, 240, "WALLET", "SUCCESS"));

        assertEquals(2,service.getAllRestaurant().size());
        verify(publisher,times(1)).publish((Restaurant) any());
        verify(publisher,times(1)).publishToOrder((OrderQueue) any());
    }

    @Test
    public void savewhenhotelinactive() throws InterruptedException {
        UUID id=UUID.randomUUID();

        Hotel hotel=new Hotel();
        hotel.setHotelId(1004);
        hotel.setHotelName("Meghna");
        hotel.setStatus("inactive");
        hrepo.save(hotel);

        when(hrepo.findById(1004)).thenReturn(Optional.of(hotel));

        OrderQueue response = OrderQueue.of(id, OrderState.ORDER_FAILED, "inactive");
        publisher.publishToOrder(response);

        service.send(Payment.of(UUID.randomUUID(), id, 20, 101, 1004, 2, 240, "WALLET", "SUCCESS"));

        //verify(publisher,times(1)).publish((OrderQueue) any());
    }

    @Test
    public void saveFailureRestaurant() throws InterruptedException {
        UUID id= UUID.randomUUID();

        when(repo.findAll()).thenReturn(Stream.of(Restaurant.of(id,1001,"UDUPI",20,"dosa",2,200,"SUCCESS"),Restaurant.of(UUID.randomUUID(),1002,"Shadab",22,"Biryani",1,200,"SUCCESS")).collect(Collectors.toList()));

        Hotel hotel=new Hotel();
        hotel.setHotelId(1004);
        hotel.setHotelName("Meghna");
        hotel.setStatus("active");

        when(hrepo.findById(1004)).thenReturn(Optional.of(hotel));

        Products product=new Products();
        product.setProductId(201);
        product.setItem("Biryani");
        product.setHotel(hotel);
        product.setQuantity(2);

        when(prodRepo.findById(201)).thenReturn(Optional.of(product));

        service.send(Payment.of(UUID.randomUUID(), id, 26, 201, 1004, 4, 800, "WALLET", "SUCCESS"));

        assertEquals(2,service.getAllRestaurant().size());
        verify(publisher,times(1)).publish((PaymentResponseFromRestaurant) any());
        verify(publisher,times(1)).publishToOrder((OrderQueue) any());
    }

}
