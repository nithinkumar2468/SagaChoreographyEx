package org.saga.example.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Table(name = "payment")
@Entity
//@Document(collection = "payment")
public class Payment {

    @Id
    private UUID paymentId;
    private UUID orderId;
    private Integer customerId;
    private Integer productId;
    private Integer hotelId;
    private Integer quantity;
    private Integer amount;
    private String paymentMethod;
    private String paymentStatus;
}
