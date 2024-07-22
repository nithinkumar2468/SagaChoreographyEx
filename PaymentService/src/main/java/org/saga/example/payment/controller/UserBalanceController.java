package org.saga.example.payment.controller;

import org.saga.example.payment.entity.UserBalance;
import org.saga.example.payment.exceptions.UserNotFoundException;
import org.saga.example.payment.repository.UserBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserBalanceController {

    @Autowired
    private UserBalanceRepository repo;

    @PostMapping("/save/userbalance")
    public UserBalance saveUserBalance(@RequestBody UserBalance balance){
        return repo.save(balance);
    }

    @GetMapping("/get/user/{id}")
    public UserBalance getUserById(@PathVariable Integer id){
        return repo.findById(id).orElseThrow(
                ()->new UserNotFoundException("User with id : "+id+" not found")
        );
    }
}
