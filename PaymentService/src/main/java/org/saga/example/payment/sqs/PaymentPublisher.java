package org.saga.example.payment.sqs;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.saga.example.orders.order.OrderQueue;
import org.saga.example.payment.entity.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentPublisher {

    private static final Logger log= LoggerFactory.getLogger(PaymentPublisher.class);

    @Autowired
    private QueueMessagingTemplate msgTemplate;

    public PaymentPublisher(AmazonSQSAsync amazonSQSAsync) {
        this.msgTemplate=new QueueMessagingTemplate(amazonSQSAsync);
    }
    public void publish(Payment payment) {
        msgTemplate.convertAndSend("restaurant-updates",payment);
    }
    public void publish(OrderQueue response){
        msgTemplate.convertAndSend("order-updates",response);
    }
}
