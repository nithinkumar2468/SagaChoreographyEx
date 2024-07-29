package org.saga.example.shared.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class OrderQueue {
    private UUID orderId;
    private OrderState orderState;
    private String message;
}
