package org.saga.example.delivery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.saga.example.delivery.entity.Delivery;
import org.saga.example.delivery.repository.DeliveryRepository;
import org.saga.example.delivery.service.DeliveryService;
import org.saga.example.delivery.sqs.OrderEventPublisher;
import org.saga.example.orders.order.OrderQueue;
import org.saga.restaurant.model.Restaurant;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = deliveryServiceTests.class)
public class deliveryServiceTests {
    @MockBean
    private DeliveryRepository repo;

    @InjectMocks
    private DeliveryService service;

    @Mock
    private OrderEventPublisher publisher;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void saveDelivery() throws InterruptedException {
        UUID id=UUID.randomUUID();
        UUID orderId=UUID.randomUUID();

        when(repo.findAll()).thenReturn(Stream.of(new Delivery(id,orderId,20,"DOSA","UDUPI",2,"SUCCESS",new Timestamp(System.currentTimeMillis()).toString()),new Delivery(UUID.randomUUID(),UUID.randomUUID(),24,"Biryani","MEGHNA",2,"SUCCESS",new Timestamp(System.currentTimeMillis()).toString())).collect(Collectors.toList()));

        service.send(Restaurant.of(id,1001,"UDUPI",20,"dosa",2,200,"SUCCESS"));

        assertEquals(2,service.getAllDeliveries().size());
        verify(publisher,times(1)).publish((OrderQueue) any());
    }
}
