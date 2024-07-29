package org.saga.example.restaurant.sqs;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import io.reactivex.rxjava3.core.Observable;
import org.saga.example.shared.order.OrderQueue;
import org.saga.example.shared.restaurant.PaymentResponseFromRestaurant;
import org.saga.example.restaurant.model.Restaurant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RestaurantPublisher {

    private static final Logger log = LoggerFactory.getLogger(RestaurantPublisher.class);

    @Autowired
    private QueueMessagingTemplate msgTemplate;

    public RestaurantPublisher(AmazonSQSAsync amazonSQSAsync) {
        this.msgTemplate = new QueueMessagingTemplate(amazonSQSAsync);
    }

    public void publishToOrder(OrderQueue response) throws InterruptedException {
        Observable.timer(50, TimeUnit.MILLISECONDS)
                .subscribe(
                        res -> {
                            msgTemplate.convertAndSend("order-updates", response);
                        }
                );

    }

    public void publishToDelivery(Restaurant resModel) {
        msgTemplate.convertAndSend("delivery-updates", resModel);
    }

    public void publishToPayment(PaymentResponseFromRestaurant res) {
        msgTemplate.convertAndSend("restaurant-failure-updates", res);
    }
}
