package org.saga.example.order.repository;

import org.saga.example.order.model.OrderPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface OrderPurchaseRepository extends JpaRepository<OrderPurchase, UUID> {
    List<OrderPurchase> findBycustomerId(Integer userId);
}

