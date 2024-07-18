package org.saga.example.order.consumer;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.saga.example.order.repository.OrderPurchaseRepository;
import org.saga.example.order.service.OrderEventHandler;
import org.saga.example.order.service.OrderPurchaseService;
import org.saga.example.shared.order.OrderQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderQueueConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderQueueConsumer.class);

    @Autowired
    private OrderPurchaseService service;

    @Autowired
    private OrderPurchaseRepository repo;

    @Autowired
    private OrderEventHandler handler;

    @Autowired
    private QueueMessagingTemplate msgTemplate;

    public OrderQueueConsumer(AmazonSQSAsync amazonSQSAsync) {
        this.msgTemplate = new QueueMessagingTemplate(amazonSQSAsync);
    }

    @SqsListener("order-updates")
    public void consume(OrderQueue response) {
        if (response != null) {
            log.info("Received Order Status = " + response.getOrderState() + " for OrderId : " + response.getOrderId());
            //log.info("{}.: "+response);
            if (response.getMessage().equalsIgnoreCase("inactive")) {
               //repo.deleteById(response.getOrderId());
               // handler.handleOrderTxn(response.getOrderId());
                service.deleteByorderId(response.getOrderId());
            } else {
                service.updateById(response);
            }
        }
    }
}
