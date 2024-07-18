package org.saga.example.payment.service;

import org.saga.example.order.model.OrderPurchase;
import org.saga.example.orders.order.OrderQueue;
import org.saga.example.orders.order.OrderState;
import org.saga.example.orders.payment.PaymentStatus;
import org.saga.example.orders.payment.TxnState;
import org.saga.example.orders.restaurant.PaymentResponseFromRestaurant;
import org.saga.example.payment.entity.Payment;
import org.saga.example.payment.entity.UserBalance;
import org.saga.example.payment.entity.UserTxn;
import org.saga.example.payment.repository.PaymentRepository;
import org.saga.example.payment.repository.UserBalanceRepository;
import org.saga.example.payment.repository.UserTxnRepository;
import org.saga.example.payment.sqs.PaymentPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository repo;

    @Autowired
    private UserBalanceRepository ubrepo;

    @Autowired
    private UserTxnRepository utrepo;
    @Autowired
    private PaymentPublisher paymentPublisher;


    @Transactional
    public void send(OrderPurchase orderPurchase) {
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID());
        payment.setOrderId(orderPurchase.getOrderId());
        payment.setCustomerId(orderPurchase.getCustomerId());
        payment.setProductId(orderPurchase.getProductId());
        payment.setHotelId(orderPurchase.getHotelId());
        payment.setQuantity(orderPurchase.getQuantity());
        payment.setPaymentMethod(orderPurchase.getPaymentMethod());
        payment.setAmount(orderPurchase.getPrice());

        //log.info(ubrepo.findAll().toString());
        //log.info(ubrepo.findById(orderPurchase.getCustomerId()).get().toString());
        UserBalance ub = ubrepo.findById(orderPurchase.getCustomerId()).get();

        if (ub.getBalance() >= orderPurchase.getPrice()) {
            ubrepo.findById(orderPurchase.getCustomerId())
                    .filter(u -> ub.getBalance() >= orderPurchase.getPrice())
                    .map(uba -> {
                        payment.setPaymentStatus(String.valueOf(PaymentStatus.SUCCESS));

                        repo.save(payment);
                        paymentPublisher.publish(payment);

                        uba.setBalance(uba.getBalance() - orderPurchase.getPrice());
                        //log.info("Amount debited from account Id : "+uba.getUserId());
                        ubrepo.save(uba);

                        OrderQueue response = OrderQueue.of(orderPurchase.getOrderId(), OrderState.ORDER_PAID,"success");
                        paymentPublisher.publish(response);

                        String time = new Timestamp(System.currentTimeMillis()).toString();
                        utrepo.save(UserTxn.of(orderPurchase.getOrderId(), uba.getUserId(), orderPurchase.getPrice(), time, TxnState.SUCCESS.toString()));
                        return true;
                    });
        } else {
            payment.setPaymentStatus(String.valueOf(PaymentStatus.FAILURE));
            repo.save(payment);
            String time = new Timestamp(System.currentTimeMillis()).toString();
            utrepo.save(UserTxn.of(orderPurchase.getOrderId(), ub.getUserId(), orderPurchase.getPrice(), time, TxnState.FAILURE.toString()));
            OrderQueue response = OrderQueue.of(orderPurchase.getOrderId(), OrderState.ORDER_FAILED,"failure");
            paymentPublisher.publish(response);
        }
    }
    public List<Payment> getAllPayments(){
        return repo.findAll();
    }

    public Payment getPaymentById(@PathVariable UUID id){
        return repo.findById(id).get();
    }

    public PaymentResponseFromRestaurant update(PaymentResponseFromRestaurant response) {
        Payment payment = repo.findById(response.getPaymentId()).get();
        payment.setPaymentStatus(String.valueOf(PaymentStatus.REFUNDED));
        repo.save(payment);

        utrepo.findById(response.getOrderId()).map(txn -> {
            txn.setTxnState(String.valueOf(PaymentStatus.REFUNDED));
            utrepo.save(txn);
            return true;
        });

        UserBalance balance = ubrepo.findById(payment.getCustomerId()).get();
        balance.setBalance(balance.getBalance() + response.getAmount());
        log.info("Amount Refunded to account Id : "+balance.getUserId());
        ubrepo.save(balance);
        return response;
    }
}