package org.saga.example.order.service;

import org.saga.example.order.model.OrderPurchase;
import org.saga.example.order.repository.OrderPurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.UUID;

@Component
public class OrderEventHandler {

    @Autowired
    private PlatformTransactionManager manager;

    @Autowired
    private OrderPurchaseService service;

    @Autowired
    private OrderPurchaseRepository repo;

    public void handleOrderTxn(UUID orderId){
        try{
            TransactionStatus status = manager.getTransaction(new DefaultTransactionDefinition());

            OrderPurchase purchase=repo.findById(orderId).get();

            manager.rollback(status);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
