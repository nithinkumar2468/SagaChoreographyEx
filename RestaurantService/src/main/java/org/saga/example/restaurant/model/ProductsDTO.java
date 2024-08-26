package org.saga.example.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ProductsDTO {
    @Id
    private Integer productId;
    private String item;
    private String category;
    private Integer price;
}
