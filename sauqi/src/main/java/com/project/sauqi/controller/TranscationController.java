package com.project.sauqi.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.project.sauqi.dao.TransactionDetailsRepo;
import com.project.sauqi.dao.TransactionRepo;
import com.project.sauqi.dao.UserRepo;
import com.project.sauqi.entity.Transaction;
import com.project.sauqi.entity.User;
import com.project.sauqi.util.EmailUtil;

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
    
    @Autowired
	private EmailUtil emailUtil;
    
//    @GetMapping
//    public Iterable<Transaction> getAllTransaction(){
//        return transactionRepo.findAll();
//    }
    
    @GetMapping("/user/{userId}")
    public Iterable<Transaction> getUserTransactions(@PathVariable int userId, @RequestParam String status){
        User findUser = userRepo.findById(userId).get();
        return transactionRepo.findTransaksiByUserIdAndStatus(userId,status);
    }
    
    @GetMapping
    public Page<Transaction> getTransactions(@RequestParam String status,Pageable pageable){
    	return transactionRepo.findTransaksiByStatus(status,pageable);
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
    	findTransaction.setTanggalReject(transaction.getTanggalReject());
    	
    	if(findTransaction.getStatus().equals("success")) {
    		findTransaction.getTransactionDetails().forEach(val -> {
    			if(val.getProducts() != null) {
    				int findQuantity = val.getQuantity();
	    			int findStockGudang = val.getProducts().getStockGudang();
	    			int findStock = val.getProducts().getStock();
	    			int findSold = val.getProducts().getSold();
	    			val.getProducts().setStockGudang(findStockGudang-findQuantity);
	    			val.getProducts().setSold(findSold + findQuantity);
	    			System.out.println(findQuantity);
	    			System.out.println(findStockGudang);
    				
//    				for(int i = 0; i < transactionRepo.findById(transactionId).get().getTransactionDetails().size() ; i++ ) {
//    	    			int findQuantity = transactionRepo.findById(transactionId).get().getTransactionDetails().get(i).getQuantity();
//    	    			int findStockGudang = transactionRepo.findById(transactionId).get().getTransactionDetails().get(i).getProducts().getStockGudang();
//    	    			int findStock = transactionRepo.findById(transactionId).get().getTransactionDetails().get(i).getProducts().getStock();
//    	    			int findSold = transactionRepo.findById(transactionId).get().getTransactionDetails().get(i).getProducts().getSold();
//    	    			findTransaction.getTransactionDetails().get(i).getProducts().setStockGudang(findStockGudang-findQuantity);
//    	    			findTransaction.getTransactionDetails().get(i).getProducts().setSold(findSold + findQuantity);
//    	    			System.out.println(findQuantity);
//    	    			System.out.println(findStockGudang);
//    	    		}
    			}else{
    					int findQuantityPaket = val.getQuantity();
    					int findStockPaket = val.getPaket().getStock();
//    					findTransaction.getTransactionDetails().get(i).getPaket().setStock();
//    					val.getPaket().getPaketDetails().forEach(value -> {
//    					value.getProducts().setStockGudang(value.getProducts().getStockGudang() - val.getQuantity());
//    				});
    			}
    		});
    		
    	}else if(findTransaction.getStatus().equals("rejectPermanent")) {
    		for(int i = 0; i < transactionRepo.findById(transactionId).get().getTransactionDetails().size() ; i++ ) {
    			int findQuantity = transactionRepo.findById(transactionId).get().getTransactionDetails().get(i).getQuantity();
    			int findStock = transactionRepo.findById(transactionId).get().getTransactionDetails().get(i).getProducts().getStock();
    			findTransaction.getTransactionDetails().get(i).getProducts().setStock(findStock+findQuantity);
    			System.out.println(findQuantity);
    			System.out.println(findStock);
    		}
    	}
    	
    	return transactionRepo.save(findTransaction);
    	}
    
    String message = "";
    @PostMapping("/sendEmailSuccess/{transactionId}")
    @Transactional
    public String emailSuccess(@PathVariable int transactionId){
    	//User findEmail = userRepo.getEmail(userId).get();
        String findTransaction = transactionRepo.findById(transactionId).get().getUser().getEmail();
        String showTransactionFullname = transactionRepo.findById(transactionId).get().getUser().getFullname();
        int showTransactionID = transactionRepo.findById(transactionId).get().getId();
        String showTransactionStatus = transactionRepo.findById(transactionId).get().getStatus();
        String showTransactionTglBeli = transactionRepo.findById(transactionId).get().getTanggalBeli();
        String showTransactionTglSuccess = transactionRepo.findById(transactionId).get().getTanggalAcc();
        int showTransactionTotalPrice = transactionRepo.findById(transactionId).get().getTotalPrice();
        
        message = "<h1>TRANSACTION SUCCESS !! !!</h1>\n ";
        message += "<h4>Selamat, " + showTransactionFullname + "</h4>";
        message += "<h4>Transaksi anda dengan No."+ showTransactionID +" telah berhasil di proses dan segera berada di tempat anda</h4>";
        message += 
        		" <table>\r\n" + 
        		"  <tr>\r\n" + 
        		"    <td>Status</td>\r\n" + 
        		"    <td>:	"+showTransactionStatus +"</td>\r\n" + 
        		"  </tr>\r\n" + 
        		
				"  <tr>\r\n" + 
				"    <td>Tanggal Beli</td>\r\n" + 
				"    <td>:	"+showTransactionTglBeli +"</td>\r\n" + 
				"  </tr>\r\n" + 

				"  <tr>\r\n" + 
				"    <td>Tanggal Success</td>\r\n" + 
				"    <td>:	"+showTransactionTglSuccess +"</td>\r\n" + 
				"  </tr>\r\n" + 

				"  <tr>\r\n" + 
				"    <td>Total Price</td>\r\n" + 
				"    <td>:	"+showTransactionTotalPrice +"</td>\r\n" + 
				"  </tr>\r\n" +
        		"</table> <br/><br/>";
        message += "Dengan detail transaksi sebagai berikut : <br/><br/>";
        
        transactionRepo.findById(transactionId).get().getTransactionDetails().forEach(val -> {
        	message += " <table>\r\n" + 
            		"  <tr>\r\n" + 
            		"    <td>Product Name</td>\r\n" + 
            		"    <td>:	"+ val.getProducts().getProductName() +"</td>\r\n" + 
            		"  </tr>\r\n" + 
            		
    				"  <tr>\r\n" + 
    				"    <td>Price per Item</td>\r\n" + 
    				"    <td>:	" + val.getProducts().getPrice() +"</td>\r\n" + 
    				"  </tr>\r\n" + 

    				"  <tr>\r\n" + 
    				"    <td>Quantity</td>\r\n" + 
    				"    <td>:	"+	val.getQuantity() +"</td>\r\n" + 
    				"  </tr>\r\n" + 

    				"  <tr>\r\n" + 
    				"    <td>Total Price</td>\r\n" + 
    				"    <td>:	" + val.getTotalPriceProduct() + "</td>\r\n" + 
    				"  </tr>\r\n" +
            		"</table> \n";
        	message += "<br/>";
        	message += "===================================================";
        	
        });
        
        emailUtil.sendEmail(findTransaction, "Transaksi Berhasil", message);
        return findTransaction;
    }
    
    @PostMapping("/sendEmailReject/{transactionId}")
    @Transactional
    public String emailReject(@PathVariable int transactionId){
    	//User findEmail = userRepo.getEmail(userId).get();
        String findTransaction = transactionRepo.findById(transactionId).get().getUser().getEmail();
        String showTransactionFullname = transactionRepo.findById(transactionId).get().getUser().getFullname();
        int showTransactionID = transactionRepo.findById(transactionId).get().getId();
        String showTransactionStatus = transactionRepo.findById(transactionId).get().getStatus();
        String showTransactionTglBeli = transactionRepo.findById(transactionId).get().getTanggalBeli();
        String showTransactionTglReject = transactionRepo.findById(transactionId).get().getTanggalReject();
        int showTransactionTotalPrice = transactionRepo.findById(transactionId).get().getTotalPrice();
        message = "<h1>REJECT PEMBELIAN !!</h1>\n ";
        message+= "Saudara " + showTransactionFullname + " Mohon Maaf Transaksi yang anda lakukan kami reject, karena bukti transfer kurang jelas, silahkan ulangi kembali dengan cara "+
        		" Lihat History -> Tab Reject -> Upload Ulang Bukti Transfer, Terima Kasih <br/>";
        message+="Berikut rincian transaksi yang kami reject : <br/><br/>";
        message+=
        		" <table>\r\n" + 
        		"  <tr>\r\n" + 
        		"    <td>Transation Id</td>\r\n" + 
        		"    <td>:	"+showTransactionID+"</td> \r\n" + 
        		"  </tr>\r\n" + 
        		"  <tr>\r\n" + 
        		"    <td>Status</td>\r\n" + 
        		"    <td>:	"+showTransactionStatus +"</td>\r\n" + 
        		"  </tr>\r\n" + 
				"  <tr>\r\n" + 
				"    <td>Tanggal Beli</td>\r\n" + 
				"    <td>:	"+showTransactionTglBeli +"</td>\r\n" + 
				"  </tr>\r\n" + 
				"  <tr>\r\n" + 
				"    <td>Tanggal Reject</td>\r\n" + 
				"    <td>:	"+showTransactionTglReject +"</td>\r\n" + 
				"  </tr>\r\n" + 
				"  <tr>\r\n" + 
				"    <td>Total Price</td>\r\n" + 
				"    <td>:	"+showTransactionTotalPrice +"</td>\r\n" + 
				"  </tr>\r\n" + 
        		"</table> \n\n";
        emailUtil.sendEmail(findTransaction, "Reject Pembelian", message);
        return findTransaction;
    }
   
    String imageDesc = "";
    @PostMapping("/sendEmailRejectPermanent/{transactionId}")
    @Transactional
    public String emailRejectPermanent(@PathVariable int transactionId) {
    	String findTransaction = transactionRepo.findById(transactionId).get().getUser().getEmail();
        String showTransactionFullname = transactionRepo.findById(transactionId).get().getUser().getFullname();
        int showTransactionID = transactionRepo.findById(transactionId).get().getId();
        String showTransactionStatus = transactionRepo.findById(transactionId).get().getStatus();
        String showTransactionTglBeli = transactionRepo.findById(transactionId).get().getTanggalBeli();
        int showTransactionTotalPrice = transactionRepo.findById(transactionId).get().getTotalPrice();
        message = "<h1>REJECT PERMANENT PEMBELIAN !!</h1>\n ";
        message+= "Saudara " + showTransactionFullname + " Mohon Maaf Transaksi yang anda lakukan kami reject permanent ";
        message+= "Dikarenakan : anda belum melakukan perubahan bukti transfer / tidak ada bukti <br/>";
        message+="Berikut rincian transaksi yang kami reject : <br/><br/>";
        message+=
        		" <table>\r\n" + 
        		"  <tr>\r\n" + 
        		"    <td>Transation Id</td>\r\n" + 
        		"    <td>:	"+showTransactionID+"</td> \r\n" + 
        		"  </tr>\r\n" + 
        		"  <tr>\r\n" + 
        		"    <td>Status</td>\r\n" + 
        		"    <td>:	"+showTransactionStatus +"</td>\r\n" + 
        		"  </tr>\r\n" + 
				"  <tr>\r\n" + 
				"    <td>Tanggal Beli</td>\r\n" + 
				"    <td>:	"+showTransactionTglBeli +"</td>\r\n" + 
				"  </tr>\r\n" + 
				"  <tr>\r\n" + 
				"    <td>Total Price</td>\r\n" + 
				"    <td>:	"+showTransactionTotalPrice +"</td>\r\n" + 
				"  </tr>\r\n" + 
        		"</table> \n\n";
        emailUtil.sendEmail(findTransaction, "Reject Permanent Pembelian", message);
        return findTransaction;
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
