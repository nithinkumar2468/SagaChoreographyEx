package org.saga.example.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Entity
@Table(name="products")
@SequenceGenerator(name="product_id_seq",initialValue =100 )
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator ="product_id_seq")
    private Integer productId;
    private String item;
    private Integer quantity;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="hotelId")
    private Hotel hotel;
}
