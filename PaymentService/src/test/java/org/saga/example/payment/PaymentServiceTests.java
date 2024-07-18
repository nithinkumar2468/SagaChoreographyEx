package org.saga.example.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.saga.example.order.model.OrderPurchase;
import org.saga.example.orders.order.OrderQueue;
import org.saga.example.orders.order.OrderState;
import org.saga.example.orders.payment.PaymentStatus;
import org.saga.example.orders.restaurant.PaymentResponseFromRestaurant;
import org.saga.example.payment.entity.Payment;
import org.saga.example.payment.entity.UserBalance;
import org.saga.example.payment.entity.UserTxn;
import org.saga.example.payment.repository.PaymentRepository;
import org.saga.example.payment.repository.UserBalanceRepository;
import org.saga.example.payment.repository.UserTxnRepository;
import org.saga.example.payment.service.PaymentService;
import org.saga.example.payment.sqs.PaymentPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = PaymentServiceTests.class)
public class PaymentServiceTests {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceTests.class);

    @Mock
    private UserBalanceRepository ubrepo;
    @Mock
    private PaymentPublisher paymentPublisher;
    @MockBean
    private PaymentRepository repo;
    @Mock
    private UserTxnRepository utrepo;

    @InjectMocks
    private PaymentService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void saveSuccessfulPayment() {
        UUID id = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID id2=UUID.randomUUID();
        int customerId = 24;

        when(repo.findAll()).thenReturn(Stream.of(Payment.of(id, orderId, customerId, 101, 1001, 2, 240, "WALLET", "SUCCESS"), Payment.of(id2, UUID.randomUUID(), 26, 104, 1002, 4, 400, "WALLET", "SUCCESS")).collect(Collectors.toList()));

        UserBalance balance = new UserBalance();
        balance.setUserId(customerId);
        balance.setBalance(400);

        OrderQueue response = OrderQueue.of(orderId, OrderState.ORDER_FAILED,"failure");
        when(ubrepo.findById(customerId)).thenReturn(Optional.of(balance));
        service.send(new OrderPurchase(orderId, customerId, 102, 1002, 2, 240, "ORDER_PAID", "WALLET", new Timestamp(System.currentTimeMillis()).toString()));

        assertEquals(2, service.getAllPayments().size());
        verify(paymentPublisher, times(1)).publish((Payment) any());
        verify(paymentPublisher, times(1)).publish((OrderQueue) any());
    }

    @Test
    public void saveFailurePayment() {
        UUID id = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        int customerId = 26;

        when(repo.findAll()).thenReturn(Stream.of(Payment.of(id, orderId, customerId, 101, 1001, 2, 240, "WALLET", "SUCCESS"), Payment.of(UUID.randomUUID(), UUID.randomUUID(), 26, 104, 1002, 4, 400, "WALLET", "SUCCESS")).collect(Collectors.toList()));

        UserBalance balance = new UserBalance();
        balance.setUserId(customerId);
        balance.setBalance(2000);

        when(ubrepo.findById(customerId)).thenReturn(Optional.of(balance));

        OrderQueue response = OrderQueue.of(orderId, OrderState.ORDER_FAILED,"failure");

        service.send(new OrderPurchase(orderId, customerId, 102, 1002, 2, 2400, "ORDER_PAID", "WALLET", new Timestamp(System.currentTimeMillis()).toString()));

        assertEquals(2, service.getAllPayments().size());
        verify(paymentPublisher, times(1)).publish((OrderQueue) any());
    }

    @Test
    public void paymentById(){
        UUID id = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        when(repo.findById(id)).thenReturn(Optional.of(Payment.of(id, orderId, 20, 101, 1001, 2, 240, "WALLET", "SUCCESS")));

        assertEquals(240,service.getPaymentById(id).getAmount());
        assertNotEquals(1002,service.getPaymentById(id).getHotelId());
        assertTrue(service.getPaymentById(id).getPaymentStatus().equalsIgnoreCase("SUCCESS"));
    }

    @Test
    public void updatePayments() {
        UUID id = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        int customerId = 20;

        when(repo.findById(id)).thenReturn(Optional.of(Payment.of(id, orderId, 20, 101, 1001, 2, 240, "WALLET", "SUCCESS")));

        OrderPurchase orderPurchase = new OrderPurchase();

        UserBalance balance = new UserBalance();
        balance.setUserId(customerId);
        balance.setBalance(400);

        when(ubrepo.findById(customerId)).thenReturn(Optional.of(balance));

        PaymentResponseFromRestaurant response = service.update(PaymentResponseFromRestaurant.of(orderId, id, 400, OrderState.valueOf("ORDER_PAID"),"success"));
        ubrepo.findById(orderPurchase.getCustomerId());

        UserTxn txn=UserTxn.of(id,customerId,400,new Timestamp(System.currentTimeMillis()).toString(), PaymentStatus.SUCCESS.toString());
        when(utrepo.findById(orderId)).thenReturn(Optional.of(txn));

        utrepo.findById(orderId).map(t->{
            t.setTxnState(PaymentStatus.REFUNDED.toString());
            utrepo.save(t);
            return true;
        });

        assertEquals(400, service.update(response).getAmount());
    }
}

