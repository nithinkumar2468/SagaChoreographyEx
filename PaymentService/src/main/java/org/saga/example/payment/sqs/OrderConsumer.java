package org.saga.example.payment.sqs;

import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.saga.example.order.model.OrderPurchase;
import org.saga.example.orders.restaurant.PaymentResponseFromRestaurant;
import org.saga.example.payment.entity.Payment;
import org.saga.example.payment.entity.UserBalance;
import org.saga.example.payment.repository.PaymentRepository;
import org.saga.example.payment.repository.UserBalanceRepository;
import org.saga.example.payment.repository.UserTxnRepository;
import org.saga.example.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    private PaymentService service;
    @Autowired
    private PaymentRepository repo;
    @Autowired
    private UserTxnRepository utrepo;

    @Autowired
    private UserBalanceRepository ubrepo;

    @SqsListener("payment-updates")
    public void consumeOrderPurchase(OrderPurchase orderPurchase) {
        log.info("Received Order Status = " + orderPurchase.getOrderStatus() + " for OrderId : " + orderPurchase.getOrderId());
        service.send(orderPurchase);
    }

    @SqsListener("restaurant-failure-updates")
    public void consumePaymentResponse(PaymentResponseFromRestaurant response) {
        if (response != null) {
            log.info("Received Order Status : " + response.getOrderState() + " from Restaurant for orderId : " + response.getOrderId());
            if (response.getMessage().equalsIgnoreCase("inactive")) {
                Payment payment=repo.findById(response.getPaymentId()).get();

                repo.deleteById(response.getPaymentId());

                UserBalance ub= ubrepo.findById(payment.getCustomerId()).get();
                ub.setBalance(ub.getBalance()+ response.getAmount());
                ubrepo.save(ub);

                utrepo.deleteById(response.getOrderId());
            } else {
                service.update(response);
            }
        }
    }
}
