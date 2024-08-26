package org.saga.example.restaurant.repository;

import org.saga.example.restaurant.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Products,Integer> {
    List<Products> findByHotelHotelId(Integer hotelId);

    List<Products> findByCategory(String category);
}
