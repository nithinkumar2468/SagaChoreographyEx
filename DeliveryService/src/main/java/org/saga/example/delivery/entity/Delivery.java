package org.saga.example.delivery.entity;

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
@Table(name="delivery")
@Entity
public class Delivery {

    @Id
    private UUID deliveryId;
    private UUID orderId;
    private Integer customerId;
    private String item;
    private String hotel;
    private Integer quantity;
    private String deliveryStatus;
    private String deliveryTime;
}
