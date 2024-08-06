package org.saga.example.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.saga.example.order.model.OrderPurchase;
import org.saga.example.order.repository.OrderPurchaseRepository;
import org.saga.example.order.service.OrderPublisher;
import org.saga.example.order.service.OrderPurchaseService;
import org.saga.example.shared.DTO.OrderPurchaseDTO;
import org.saga.example.shared.order.OrderQueue;
import org.saga.example.shared.order.OrderState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = OrderServiceTests.class)
public class OrderServiceTests {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceTests.class);

    @Mock
    private OrderPublisher publisher;
    @InjectMocks
    private OrderPurchaseService service;

    @MockBean
    private OrderPurchaseRepository repo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void saveOrders() throws JsonProcessingException {
        UUID id=UUID.randomUUID();
        when(repo.findAll()).thenReturn(Stream.of(new OrderPurchase(id, 22, 201, 1004, 2, 240, String.valueOf(OrderState.ORDER_PAID), "WALLET",Instant.now()), new OrderPurchase(UUID.randomUUID(), 24, 202, 1002, 4, 400, String.valueOf(OrderState.ORDER_PAID), "WALLET",Instant.now())).collect(Collectors.toList()));

        OrderPurchaseDTO dto=OrderPurchaseDTO.of(id,22,240,202,1004,240, "WALLET");
        service.createOrderPurchase(dto);

        //doThrow(new HotelInactiveException("Hotel InActive For orderId : "+id)).when(repo).deleteById(id);
        //assertThatThrownBy(() -> service.createOrderPurchase(dto))
        //        .isInstanceOf(RuntimeException.class);
        assertEquals(2, service.getAllOrders().size());
        verify(publisher, times(1)).publish(any());
    }

    @Test
    public void GetAllOrders() {

        UUID id = UUID.randomUUID();
        when(repo.findAll()).thenReturn(Stream.of(new OrderPurchase(id, 22, 201, 1004, 2, 240, String.valueOf(OrderState.ORDER_PAID), "WALLET",Instant.now()), new OrderPurchase(UUID.randomUUID(), 24, 202, 1002, 4, 400, String.valueOf(OrderState.ORDER_PAID), "WALLET",Instant.now())).collect(Collectors.toList()));

        log.info(repo.findAll().toString());

        assertEquals(2, service.getAllOrders().size());
    }
    @Test
    public void GetById() {

        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.of(new OrderPurchase(id, 26, 202, 1004, 4, 400, String.valueOf(OrderState.ORDER_DELIVERED), "WALLET",Instant.now())));

        assertEquals(202, service.getByID(id).getProductId());
        assertNotEquals("Order_PAID", service.getByID(id).getOrderStatus());
        assertEquals("WALLET", service.getByID(id).getPaymentMethod());
        assertNotNull(service.getByID(id));
    }

    @Test
    public void UpdateById() {

        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.of(new OrderPurchase(id, 26, 202, 1004, 4, 400, String.valueOf(OrderState.ORDER_PAID), "WALLET",Instant.now())));

        OrderQueue response=service.updateById(OrderQueue.of(id,OrderState.ORDER_DELIVERED,"success"));

        assertEquals("ORDER_DELIVERED".toString(),String.valueOf(service.updateById(response).getOrderState()));
        assertNotEquals(300, service.getByID(id).getPrice());
    }

    @Test
    public void DeleteById(){

        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.of(new OrderPurchase(id, 26, 202, 1004, 4, 400, String.valueOf(OrderState.ORDER_PAID), "WALLET",Instant.now())));

        service.deleteByorderId(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    public void GetByCustomerId(){
        UUID id=UUID.randomUUID();
        when(repo.findBycustomerId(20)).thenReturn(Stream.of(new OrderPurchase(id, 20, 201, 1004, 2, 240, String.valueOf(OrderState.ORDER_PAID), "WALLET",Instant.now()), new OrderPurchase(UUID.randomUUID(), 20, 202, 1002, 4, 400, String.valueOf(OrderState.ORDER_PAID), "WALLET",Instant.now())).collect(Collectors.toList()));

        assertEquals(2,service.GetAllOrdersByUserId(20).size());
    }
}
