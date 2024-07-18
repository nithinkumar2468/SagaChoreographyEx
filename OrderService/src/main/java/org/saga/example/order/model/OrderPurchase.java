package org.saga.example.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="orderpurchase")
@Entity
public class OrderPurchase {

    @Id
    private UUID orderId;
    private Integer customerId;
    private Integer productId;
    private Integer hotelId;
    private Integer quantity;
    private Integer price;
    private String orderStatus;
    private String paymentMethod;
    private String createdTimeStamp;
}
