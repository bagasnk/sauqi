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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sauqi.dao.PaketRepo;
import com.project.sauqi.dao.ProductRepo;
import com.project.sauqi.entity.Paket;
import com.project.sauqi.entity.Product;

@RestController
@RequestMapping("/paket")
@CrossOrigin
public class PaketController {
	
	private String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\paket\\";
	
	@Autowired
    private PaketRepo paketRepo;
	
	@Autowired
	private ProductRepo productRepo;
	
//	@PostMapping("/addPaket")
//	public Paket addPaket(@RequestBody Paket paket) {
//		return paketRepo.save(paket);
//	}
	
	@PutMapping("/edit/{paketId}")
	public String editProduct(@RequestParam("file") Optional<MultipartFile> file, @RequestParam("paketData") String paketString ,@PathVariable int paketId) throws JsonMappingException, JsonProcessingException {
		Date date = new Date();
		Paket findPaket = paketRepo.findById(paketId).get();
		findPaket = new ObjectMapper().readValue(paketString , Paket.class); 
		String fileDownloadUri = findPaket.getImagePaket();
		
		if(file.toString() != "Optional.empty") {
			
		String fileExtension = file.get().getContentType().split("/")[1];
		System.out.println(fileExtension);
		String fileName = "PAKET-" + date.getTime() + "." +  fileExtension;
		Path path = Paths.get(StringUtils.cleanPath(uploadPath)  + fileName);
		try {
			Files.copy(file.get().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/paket/download/")
				.path(fileName).toUriString();
		}
		findPaket.setImagePaket(fileDownloadUri);
		System.out.println(findPaket.getPaketDetails().size());;
//		findPaket.setPaketDetails(findPaket.getPaketDetails());
//		System.out.println(stockGudangFE + " " + findProduct.getStock() + " " + findProduct.getStockGudang());
//		if(findProduct.getStock() != findProduct.getStockGudang()) {
//			findProduct.setStock(findProduct.getStockGudang() - (stockGudangFE - findProduct.getStock()));
//			findProduct.setStockGudang(findProduct.getStockGudang());
//		}
		paketRepo.save(findPaket);
		return fileDownloadUri;
	}
	
	
	
	@PostMapping("/addPaket")
	public String uploadFile(@RequestParam("file") Optional<MultipartFile> file, @RequestParam("paketData") String paketString) throws JsonMappingException, JsonProcessingException {
		Date date = new Date();
		Paket paket = new ObjectMapper().readValue(paketString , Paket.class);
		String fileDownloadUri = "no image";
		
		if(file.toString() != "Optional.empty") {
		String fileExtension = file.get().getContentType().split("/")[1];
		System.out.println(fileExtension);
		String newFileName = "PAKET-" + date.getTime() + "." +  fileExtension;
		
		String fileName = StringUtils.cleanPath(newFileName);
		
		Path path = Paths.get(StringUtils.cleanPath(uploadPath)  + fileName);
		
		try {
			Files.copy(file.get().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/paket/download/")
				.path(fileName).toUriString();
		}
		
		paket.setImagePaket(fileDownloadUri);
		paket.setStock(paket.getStock());
		paketRepo.save(paket);
		return fileDownloadUri;
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
	
	@GetMapping
	public Iterable<Paket> getAllPaket(){
      return paketRepo.findAll();
	}
	
	@GetMapping("/{minPrice}/{maxPrice}/{orderBy}/{sortList}")
	public Page<Paket> findPaketByPrice
		(
			@PathVariable double minPrice, 
			@PathVariable double maxPrice, 
			@RequestParam String paketName,
			@PathVariable String orderBy, 
			@PathVariable String sortList,
			Pageable pageable
		){
		if (maxPrice == 0) {
			maxPrice = 9999999;
		}
			if (orderBy.equals("paketName") && sortList.equals("asc")) {
				return paketRepo.findPaketByPriceOrderByPaketNameAsc(minPrice, maxPrice, paketName, pageable);
			}else if (orderBy.equals("paketName") && sortList.equals("desc")) {
				return paketRepo.findPaketByPriceOrderByPaketNameDesc(minPrice, maxPrice, paketName, pageable);
			}else if (orderBy.equals("price") && sortList.equals("asc")) {
				return paketRepo.findPaketByPriceOrderByPriceAsc(minPrice, maxPrice, paketName, pageable);
			}else{
				return paketRepo.findPaketByPriceOrderByPriceDesc(minPrice, maxPrice, paketName, pageable);
			}
		}
	
	@GetMapping("/{id}")
	public Paket getPaketById(@PathVariable int id) {
		Paket findPaket = paketRepo.findById(id).get();
		return findPaket;
	}
	
	@DeleteMapping("/deleteProductPaket/{paketId}/{productId}")
	public Paket deleteProductPaket(@PathVariable int paketId,@PathVariable int productId) {
		Paket findPaket = paketRepo.findById(paketId).get();
		Product findProduct = productRepo.findById(productId).get();
		findPaket.getPaketDetails().remove(findProduct);
		return paketRepo.save(findPaket);
	}
	

}
