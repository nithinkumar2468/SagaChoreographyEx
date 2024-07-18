package org.saga.example.shared.restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.saga.example.shared.order.OrderState;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class PaymentResponseFromRestaurant {
    private UUID orderId;
    private UUID paymentId;
    private Integer amount;
    private OrderState orderState;
    private String message;
}
