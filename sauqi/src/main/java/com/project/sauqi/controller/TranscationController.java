package com.project.sauqi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.sauqi.dao.TransactionDetailsRepo;
import com.project.sauqi.dao.TransactionRepo;
import com.project.sauqi.dao.UserRepo;
import com.project.sauqi.entity.Transaction;
import com.project.sauqi.entity.User;

@RestController
@RequestMapping("/transaction")
@CrossOrigin
public class TranscationController {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
    private TransactionDetailsRepo transactionDetailsRepo;

    @Autowired
    private TransactionRepo transactionRepo;
    
    @GetMapping
    public Iterable<Transaction> getAllTransaction(){
        return transactionRepo.findAll();
    }
    
    @GetMapping("/user/{userId}")
    public Iterable<Transaction> getUserTransactions(@PathVariable int userId, @RequestParam String status){
        User findUser = userRepo.findById(userId).get();
        return transactionRepo.findTransaksiByStatus(userId, status);
    }
    
    
    
    @PostMapping("/addTransaction/{userId}")
	public Transaction addTransaction(@RequestBody Transaction transaction,@PathVariable int userId) {
        User findUser = userRepo.findById(userId).get();
        transaction.setUser(findUser);
		return transactionRepo.save(transaction);
	}
    
    
    
}
