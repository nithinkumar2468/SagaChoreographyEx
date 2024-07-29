package org.saga.example.order.exceptions;

import com.amazonaws.services.sqs.model.AmazonSQSException;

public class AwsSqsException extends AmazonSQSException {
    public AwsSqsException(String message) {
        super(message);
    }
}
