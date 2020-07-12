package com.project.sauqi.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sauqi.dao.TransactionDetailsRepo;
import com.project.sauqi.dao.TransactionRepo;
import com.project.sauqi.dao.UserRepo;
import com.project.sauqi.entity.Product;
import com.project.sauqi.entity.Transaction;
import com.project.sauqi.entity.User;

@RestController
@RequestMapping("/transaction")
@CrossOrigin
public class TranscationController {
	
	private String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\buktiTransfer\\";

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
    private TransactionDetailsRepo transactionDetailsRepo;

    @Autowired
    private TransactionRepo transactionRepo;
    
//    @GetMapping
//    public Iterable<Transaction> getAllTransaction(){
//        return transactionRepo.findAll();
//    }
    
    @GetMapping("/user/{userId}")
    public Iterable<Transaction> getUserTransactions(@PathVariable int userId, @RequestParam String status){
        User findUser = userRepo.findById(userId).get();
        return transactionRepo.findTransaksiByUserIdAndStatus(userId, status);
    }
    
    @GetMapping
    public Iterable<Transaction> getTransactions(@RequestParam String status){
    	return transactionRepo.findTransaksiByStatus(status);
    }
    
    @PostMapping("/addTransaction/{userId}")
	public Transaction addTransaction(@RequestBody Transaction transaction,@PathVariable int userId) {
        User findUser = userRepo.findById(userId).get();
        transaction.setUser(findUser);
		return transactionRepo.save(transaction);
	}
    
    @PatchMapping("/editTransaction/{transactionId}")
    public Transaction editTransaction(@RequestBody Transaction transaction,@PathVariable int transactionId) {
    	Transaction findTransaction = transactionRepo.findById(transactionId).get();
    	findTransaction.setStatus(transaction.getStatus());
    	findTransaction.setTanggalAcc(transaction.getTanggalAcc());
    	return transactionRepo.save(findTransaction);
    }
    
    @PutMapping("/uploadBukti/{transactionId}")
	public Transaction  uploadBuktiTransfer(@RequestParam ("file") Optional<MultipartFile> file, @PathVariable int transactionId ) {
		
		Transaction findTransaction = transactionRepo.findById(transactionId).get();
		Date date = new Date();
		
		String fileDownloadUri = "no image";
		
		if (file.toString()!="Optional.empty") {
			String fileExtension = file.get().getContentType().split("/")[1];
			String newFileName = "TRF-"+ date.getTime()+ "." + fileExtension;
			
			String fileName = StringUtils.cleanPath(newFileName);
			
			Path path = Paths.get(StringUtils.cleanPath(uploadPath) + fileName);
			
			try {
				Files.copy(file.get().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
			

			fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/transaction/download/")
					.path(fileName).toUriString();
		}
		findTransaction.setId(findTransaction.getId());
		findTransaction.setStatus("pending");
		findTransaction.setBuktiTrf(fileDownloadUri);
		findTransaction.setTransactionDetails(findTransaction.getTransactionDetails());
		transactionRepo.save(findTransaction);
		return transactionRepo.save(findTransaction);
	}
    
    @GetMapping("/download/{fileName:.+}")
	public ResponseEntity<Object> downloadFile(@PathVariable String fileName){
		Path path = Paths.get(uploadPath, fileName);
		Resource resource = null;
		
		try {
			resource = new UrlResource(path.toUri());
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}
			return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		
	}
    
    
    
}
