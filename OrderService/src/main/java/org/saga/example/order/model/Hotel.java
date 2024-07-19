package org.saga.example.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Entity
@Table(name="hotel")
@SequenceGenerator(name="hotel_id_seq", initialValue=1000)
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator="hotel_id_seq")
    private Integer hotelId;
    private String hotelName;
    private String status;
}
