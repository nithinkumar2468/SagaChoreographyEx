package org.saga.example.payment.controller;

import org.saga.example.payment.entity.LoginFront;
import org.saga.example.payment.entity.LoginMessage;
import org.saga.example.payment.entity.UserBalance;
import org.saga.example.payment.exceptions.UserNotFoundException;
import org.saga.example.payment.repository.UserBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:3000")
public class UserBalanceController {

    @Autowired
    private UserBalanceRepository repo;

    @PostMapping("/save/userbalance")
    public UserBalance saveUserBalance(@RequestBody UserBalance balance){
        return repo.save(balance);
    }

    @PostMapping(path = "/users/login")
    LoginMessage loginuser(@RequestBody LoginFront loginFront) {
        UserBalance email = repo.findByEmail(loginFront.getEmail());
        if (email != null) {
            String password = loginFront.getPassword();
            String userpass = email.getPassword();
            if (password.matches(userpass)) {
                return new LoginMessage("Login Success", true);
            } else {
                return new LoginMessage("Incorrect emailId or Password", false);
            }
        } else {
            return new LoginMessage("emailId not exist", false);
        }
    }

    @GetMapping("/getuseremail/{email}")
    public UserBalance getUserIdByEmail(@PathVariable String email){
        UserBalance user=repo.findByEmail(email);
        return user;
    }

    @GetMapping("/get/user/{id}")
    public UserBalance getUserById(@PathVariable Integer id){
        return repo.findById(id).orElseThrow(
                ()->new UserNotFoundException("User with id : "+id+" not found")
        );
    }
}
