package org.saga.example.delivery.sqs;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import io.reactivex.rxjava3.core.Observable;
import org.saga.example.orders.order.OrderQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OrderEventPublisher {

    public static final Logger log= LoggerFactory.getLogger(OrderEventPublisher.class);

    @Autowired
    private QueueMessagingTemplate msgTemplate;

    public OrderEventPublisher(AmazonSQSAsync amazonSQSAsync){
        this.msgTemplate=new QueueMessagingTemplate(amazonSQSAsync);
    }

    public void publish(OrderQueue request) {
        log.info("Publishing Event to Order_Updates Queue for orderId : "+request.getOrderId());

        Observable.timer(200, TimeUnit.MILLISECONDS)
                        .subscribe(res->{
                            msgTemplate.convertAndSend("order-updates",request);
                        });
    }
}
