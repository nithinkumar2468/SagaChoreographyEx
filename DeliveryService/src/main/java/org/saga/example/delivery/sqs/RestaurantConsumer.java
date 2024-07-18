package org.saga.example.delivery.sqs;

import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.saga.example.delivery.service.DeliveryService;
import org.saga.restaurant.model.Restaurant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantConsumer {

    private static final Logger log= LoggerFactory.getLogger(RestaurantConsumer.class);

    @Autowired
    private DeliveryService service;

    @SqsListener("delivery-updates")
    public void consume(Restaurant request) throws InterruptedException {
        log.info("Received Order Status for orderId :" +request.getOrderId());
        service.send(request);
    }
}
