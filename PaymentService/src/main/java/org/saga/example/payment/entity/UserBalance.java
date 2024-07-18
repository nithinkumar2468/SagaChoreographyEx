package org.saga.example.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="userbalance")
@SequenceGenerator(name="user_id_seq", initialValue=20)
public class UserBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="user_id_seq")
    private int userId;

    private int balance;
}
