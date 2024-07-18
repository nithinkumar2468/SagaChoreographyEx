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
@Entity
@Table(name="usertxn")
//@Document(collection = "usertxn")
public class UserTxn {
    @Id
    private UUID orderId;
    private int userId;
    private int amount;
    private String orderedDate;
    private String txnState;
}
