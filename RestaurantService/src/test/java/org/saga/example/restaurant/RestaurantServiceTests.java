package org.saga.example.restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.saga.example.payment.entity.Payment;
import org.saga.example.restaurant.model.Hotel;
import org.saga.example.restaurant.model.Products;
import org.saga.example.restaurant.model.Restaurant;
import org.saga.example.restaurant.repository.HotelRepository;
import org.saga.example.restaurant.repository.ProductsRepository;
import org.saga.example.restaurant.repository.RestaurantRepository;
import org.saga.example.restaurant.service.RestaurantService;
import org.saga.example.restaurant.sqs.RestaurantPublisher;
import org.saga.example.shared.order.OrderQueue;
import org.saga.example.shared.order.OrderState;
import org.saga.example.shared.restaurant.PaymentResponseFromRestaurant;
import org.saga.example.shared.restaurant.UpdateOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = RestaurantServiceTests.class)
public class RestaurantServiceTests {

    private static final Logger log = LoggerFactory.getLogger(RestaurantServiceTests.class);

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
    @Mock
    private ModelMapper  modelMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void saveSuccessRestaurant() throws InterruptedException {
        UUID id = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();

        when(hrepo.findById(1000)).thenReturn(Optional.of(Hotel.of(1000, "UDUPI", "1001", "Active")));

        when(repo.findAll()).thenReturn(Stream.of(Restaurant.of(id, 1000, "UDUPI", 20, paymentId, "dosa", 2, 200, "SUCCESS"), Restaurant.of(UUID.randomUUID(), 1000, "Shadab", 22, UUID.randomUUID(), "Biryani", 1, 200, "SUCCESS")).collect(Collectors.toList()));

        Products product = new Products();
        product.setProductId(101);
        product.setItem("DOSA");
        product.setHotel(Hotel.of(1000, "UDUPI", "1001", "Active"));
        product.setCategory("tiffins");
        product.setPrice(70);

        when(prodRepo.findById(101)).thenReturn(Optional.of(product));

        service.send(Payment.of(paymentId,id,20,101,1000,2,140,"WALLET","SUCCESS"));

        assertEquals(2, service.getAllRestaurant().size());
        verify(publisher, times(1)).publishToOrder((OrderQueue) any());
    }

    @Test
    public void getAllhotels(){
        UUID id = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();

        when(repo.findAll()).thenReturn(Stream.of(Restaurant.of(id, 1001, "UDUPI", 20, paymentId, "dosa", 2, 200, "SUCCESS"), Restaurant.of(UUID.randomUUID(), 1002, "Shadab", 22, UUID.randomUUID(),"Biryani", 1, 200, "SUCCESS")).collect(Collectors.toList()));

        assertEquals(2,service.getAllRestaurant().size());
    }

    @Test
    public void getOrderById() {
        UUID orderId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();

        when(repo.findById(orderId)).thenReturn(Optional.of(Restaurant.of(orderId, 1000, "UDUPI", 20, paymentId, "DOSA", 2, 140, "SUCCESS")));

        assertEquals(140,service.getOrderById(orderId).getPrice());
    }

    @Test
    public void updateOrderwhenSuccess() throws InterruptedException {
        UUID orderId=UUID.randomUUID();
        UUID paymentId=UUID.randomUUID();

        when(repo.findById(orderId)).thenReturn(Optional.of(Restaurant.of(orderId,1000,"UDUPI",20,paymentId,"DOSA",2,140,"SUCCESS")));

        UpdateOrderRequest request=UpdateOrderRequest.of("success");
        service.updateOrderByOrderId(request,orderId);

        OrderQueue response = OrderQueue.of(orderId, OrderState.ORDER_PREPARED, "success");

        verify(publisher, times(1)).publishToOrder((OrderQueue) any());
        verify(publisher,times(1)).publishToDelivery((Restaurant) any());

        assertTrue(service.getOrderById(orderId).getRestaurantOrderStatus().equalsIgnoreCase("success"));
    }

    @Test
    public void updateOrderwhenFailure() throws InterruptedException {
        UUID orderId=UUID.randomUUID();
        UUID paymentId=UUID.randomUUID();

        when(repo.findById(orderId)).thenReturn(Optional.of(Restaurant.of(orderId,1000,"UDUPI",20,paymentId,"DOSA",2,140,"SUCCESS")));

        UpdateOrderRequest request=UpdateOrderRequest.of("failure");
        service.updateOrderByOrderId(request,orderId);

        OrderQueue response = OrderQueue.of(orderId, OrderState.ORDER_FAILED, "failure");

        verify(publisher, times(1)).publishToOrder((OrderQueue) any());
        verify(publisher,times(1)).publishToPayment((PaymentResponseFromRestaurant) any());

        assertTrue(service.getOrderById(orderId).getRestaurantOrderStatus().equalsIgnoreCase("failure"));
    }
}
   /*
     @Test
    public void savewhenhotelinactive() throws InterruptedException {
        UUID id = UUID.randomUUID();

        Hotel hotel = new Hotel();
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
        UUID id = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();

        when(repo.findAll()).thenReturn(Stream.of(Restaurant.of(id, 1001, "UDUPI", 20, paymentId, "dosa", 2, 200, "SUCCESS"), Restaurant.of(UUID.randomUUID(), 1002, "Shadab", 22, UUID.randomUUID(),"Biryani", 1, 200, "SUCCESS")).collect(Collectors.toList()));

        Hotel hotel = new Hotel();
        hotel.setHotelId(1004);
        hotel.setHotelName("Meghna");
        hotel.setStatus("active");

        when(hrepo.findById(1004)).thenReturn(Optional.of(hotel));

        Products product = new Products();
        product.setProductId(201);
        product.setItem("Biryani");
        product.setHotel(hotel);
        product.setQuantity(2);

        when(prodRepo.findById(201)).thenReturn(Optional.of(product));

        service.send(Payment.of(UUID.randomUUID(), id, 26, 201, 1004, 4, 800, "WALLET", String.valueOf(PaymentStatus.SUCCESS)));

        assertEquals(2, service.getAllRestaurant().size());
        verify(publisher, times(1)).publishToPayment((PaymentResponseFromRestaurant) any());
        verify(publisher, times(1)).publishToOrder((OrderQueue) any());
    }*/