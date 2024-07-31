package org.saga.example.order.model;

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

    @JsonDeserialize(using= DefaultInstantDeserializer.class)
    @JsonSerialize(using= DefaultInstantSerializer.class)
    private Instant createdTimeStamp;
}
