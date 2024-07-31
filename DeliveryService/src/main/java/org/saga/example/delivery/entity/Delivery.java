package org.saga.example.delivery.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.saga.example.shared.order.DefaultInstantDeserializer;
import org.saga.example.shared.order.DefaultInstantSerializer;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery")
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

    @JsonDeserialize(using = DefaultInstantDeserializer.class)
    @JsonSerialize(using = DefaultInstantSerializer.class)
    private Instant deliveryTime;
}