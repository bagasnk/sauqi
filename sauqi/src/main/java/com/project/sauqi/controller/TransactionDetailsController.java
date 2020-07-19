package com.project.sauqi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sauqi.dao.PaketRepo;
import com.project.sauqi.dao.ProductRepo;
import com.project.sauqi.dao.TransactionDetailsRepo;
import com.project.sauqi.dao.TransactionRepo;
import com.project.sauqi.entity.Paket;
import com.project.sauqi.entity.Product;
import com.project.sauqi.entity.Transaction;
import com.project.sauqi.entity.TransactionDetails;

@RestController
@RequestMapping("/transactionDetails")
@CrossOrigin
public class TransactionDetailsController {
	
	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
    private TransactionDetailsRepo transactionDetailsRepo;

    @Autowired
    private TransactionRepo transactionRepo;
    
    @Autowired 
    private PaketRepo paketRepo;
    
    @GetMapping
    public Iterable<TransactionDetails> getAllTransactionDetails(){
        return transactionDetailsRepo.findAll();
    }
    
    @PostMapping("/addTransactionDetails/{transactionId}/{productId}")
	public TransactionDetails addTransactionDetails(@RequestBody TransactionDetails transactionDetails, @PathVariable int transactionId, @PathVariable int productId) {
        Transaction findTransaction = transactionRepo.findById(transactionId).get();
        Product findProduct = productRepo.findById(productId).get();
        transactionDetails.setTransaction(findTransaction);
        transactionDetails.setProducts(findProduct);
        
        return transactionDetailsRepo.save(transactionDetails);
    }
    
    @PostMapping("/addTransactionDetailsPaket/{transactionId}/{paketId}")
	public TransactionDetails addTransactionDetailsPaket(@RequestBody TransactionDetails transactionDetails, @PathVariable int transactionId, @PathVariable int paketId) {
        Transaction findTransaction = transactionRepo.findById(transactionId).get();
        Paket findPaket = paketRepo.findById(paketId).get();
        transactionDetails.setTransaction(findTransaction);
        transactionDetails.setPaket(findPaket);
        return transactionDetailsRepo.save(transactionDetails);
    }
}
