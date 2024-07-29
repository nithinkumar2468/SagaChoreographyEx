package org.saga.example.shared.restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDTO {
    private UUID orderId;
    private Integer userId;
    private String Item;
    private Integer quantity;
    private Integer price;
    private String restaurantOrderStatus;
}
