package org.saga.example.payment.repository;

import org.saga.example.payment.entity.UserTxn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserTxnRepository extends JpaRepository<UserTxn, UUID> {
}
