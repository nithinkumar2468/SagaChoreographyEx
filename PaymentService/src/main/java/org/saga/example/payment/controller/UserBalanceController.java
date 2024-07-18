package org.saga.example.payment.controller;

import org.saga.example.payment.entity.UserBalance;
import org.saga.example.payment.repository.UserBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserBalanceController {

    @Autowired
    private UserBalanceRepository repo;

    @PostMapping("/save/userbalance")
    public UserBalance saveUserBalance(@RequestBody UserBalance balance){
        return repo.save(balance);
    }
}
