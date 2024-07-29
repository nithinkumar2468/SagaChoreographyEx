package org.saga.example.order.service;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.saga.example.order.exceptions.AwsSqsException;
import org.saga.example.order.exceptions.OrderNotFoundException;
import org.saga.example.order.model.OrderPurchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderPublisher {
    private static final Logger log = LoggerFactory.getLogger(OrderPublisher.class);
    private QueueMessagingTemplate msgTemplate;

    public OrderPublisher(AmazonSQSAsync amazonSQSAsync) {
        this.msgTemplate = new QueueMessagingTemplate(amazonSQSAsync);
    }
    public void publish(OrderPurchase orderpurchase) {
            log.info("Order Event Triggered :{}", orderpurchase.getOrderId());
            msgTemplate.convertAndSend("payment-updates", orderpurchase);
    }
}
