package org.saga.example.shared.restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class UpdateOrderRequest {
    private String restaurantOrderStatus;
}
