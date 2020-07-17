package com.project.sauqi.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
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
import com.project.sauqi.dao.CategoriesRepo;
import com.project.sauqi.dao.PagingRepo;
import com.project.sauqi.dao.ProductRepo;
import com.project.sauqi.entity.Categories;
import com.project.sauqi.entity.Product;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
	
	private String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\products\\";

	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
	private CategoriesRepo categoriesRepo;
	
	@Autowired
    private PagingRepo pagingRepo;
	
	
	@GetMapping 
	public Iterable<Product> getAllProject(){ 
		return productRepo.findAll(); 
	} 
	
	@GetMapping("/{categoriesId}/products")
	public List<Product> getProductOfCategories(@PathVariable int categoriesId) {
		Categories findCategories = categoriesRepo.findById(categoriesId).get();
		return findCategories.getProduct();
	}
	
	@GetMapping("/{id}")
	public Product getProductById(@PathVariable int id) {
		Product findProduct = productRepo.findById(id).get();
		return findProduct;
	}
	
//	@PostMapping("/addProduct")
//	public Product addProduct(@RequestBody Product product) {
//		return productRepo.save(product);
//	}

	@PostMapping("/{productsId}/categories/{categoriesId}")
	public Product addCategoryToProducts(@PathVariable int productsId, @PathVariable int categoriesId) {
		Product findProduct = productRepo.findById(productsId).get();
		Categories findCategories = categoriesRepo.findById(categoriesId).get();
		findProduct.getCategories().add(findCategories);
		return productRepo.save(findProduct);
	}
	
	
	@DeleteMapping("/{productId}")
	public void deleteProduct(@PathVariable int productId) {
		Product findProduct= productRepo.findById(productId).get();
		
		findProduct.getCategories().forEach(categories -> {
			List<Product> categoriesMovies = categories.getProduct();
			categoriesMovies.remove(findProduct);
			categoriesRepo.save(categories);
		});
		findProduct.setCategories(null);
		productRepo.deleteById(productId);
	}
	
	@DeleteMapping("/{productId}/categories/{categoriesId}")
	public Product deleteProductCategory(@PathVariable int productId,@PathVariable int categoriesId) {
		Product findProduct = productRepo.findById(productId).get();
		Categories findCategories = categoriesRepo.findById(categoriesId).get();
		
		findProduct.getCategories().remove(findCategories);
		return productRepo.save(findProduct);
	}
	
//	@PutMapping("/edit")
//	public Product editProduct(@RequestBody Product product) {
//		Product findProduct= productRepo.findById(product.getId()).get();
//		product.setCategories(findProduct.getCategories());
//		return productRepo.save(product);
//	}
	
	@GetMapping("/{minPrice}/{maxPrice}/{orderBy}/{sortList}")
	public Page<Product> findProductByPrice
		(
			@PathVariable double minPrice, 
			@PathVariable double maxPrice, 
			@RequestParam String productName,
			@PathVariable String orderBy, 
			@PathVariable String sortList,
			Pageable pageable
		){
		if (maxPrice == 0) {
			maxPrice = 9999999;
		}
			if (orderBy.equals("productName") && sortList.equals("asc")) {
				return productRepo.findProductByPriceOrderByProductNameAsc(minPrice, maxPrice, productName, pageable);
			}else if (orderBy.equals("productName") && sortList.equals("desc")) {
				return productRepo.findProductByPriceOrderByProductNameDesc(minPrice, maxPrice, productName, pageable);
			}else if (orderBy.equals("price") && sortList.equals("asc")) {
				return productRepo.findProductByPriceOrderByPriceAsc(minPrice, maxPrice, productName, pageable);
			}else{
				return productRepo.findProductByPriceOrderByPriceDesc(minPrice, maxPrice, productName, pageable);
			}
		}
	
	@GetMapping("/{minPrice}/category/{maxPrice}/{orderBy}/{sortList}")
		public Page<Product> findProductWithFilter
			(
			@PathVariable double minPrice, 
			@PathVariable double maxPrice, 
			@RequestParam String productName, 
			@RequestParam String nama, 
			@PathVariable String orderBy, 
			@PathVariable String sortList,
			Pageable pageable
			){
		
		if (maxPrice == 0) {
			maxPrice = 9999999;
		}
			if (orderBy.equals("productName") && sortList.equals("asc")) {
				return productRepo.findProductCategoryByPriceOrderByProductNameAsc(minPrice, maxPrice, productName, nama , pageable);
			}
			else if (orderBy.equals("productName") && sortList.equals("desc")) {
				return productRepo.findProductCategoryByPriceOrderByProductNameDesc(minPrice, maxPrice, productName, nama, pageable);
			}
			else if (orderBy.equals("price") && sortList.equals("asc")) {
				return productRepo.findProductCategoryByPriceOrderByPriceAsc(minPrice, maxPrice, productName, nama, pageable);
			}else{
				return productRepo.findProductCategoryByPriceOrderByPriceDesc(minPrice, maxPrice, productName, nama, pageable);
			}
		}
		
	@GetMapping("/pages")
	 public Page<Product> getAllProduct(@RequestParam String productName, Pageable pageable){
	    return productRepo.findProductAdminByName(productName,pageable);
	 }
	
	@PutMapping("/edit/{productId}")
	public String editProduct(@RequestParam("file") Optional<MultipartFile> file, @RequestParam("productData") String productString ,@PathVariable int productId , @RequestParam int stockGudangFE) throws JsonMappingException, JsonProcessingException {
		Date date = new Date();
		Product findProduct = productRepo.findById(productId).get();
		findProduct = new ObjectMapper().readValue(productString , Product.class); 
		String fileDownloadUri = findProduct.getImage();
		
		if(file.toString() != "Optional.empty") {
			
		String fileExtension = file.get().getContentType().split("/")[1];
		System.out.println(fileExtension);
		String fileName = "PROD-" + date.getTime() + "." +  fileExtension;
		Path path = Paths.get(StringUtils.cleanPath(uploadPath)  + fileName);
		try {
			Files.copy(file.get().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/products/download/")
				.path(fileName).toUriString();
		}
		findProduct.setImage(fileDownloadUri);
		System.out.println(stockGudangFE + " " + findProduct.getStock() + " " + findProduct.getStockGudang());
		if(findProduct.getStock() != findProduct.getStockGudang()) {
			findProduct.setStock(findProduct.getStockGudang() - (stockGudangFE - findProduct.getStock()));
			findProduct.setStockGudang(findProduct.getStockGudang());
		}
		productRepo.save(findProduct);
		return fileDownloadUri;
	}
	
	
	
	@PostMapping("/addProduct")
	public String uploadFile(@RequestParam("file") Optional<MultipartFile> file, @RequestParam("productData") String productString) throws JsonMappingException, JsonProcessingException {
		Date date = new Date();
		Product product = new ObjectMapper().readValue(productString , Product.class);
		String fileDownloadUri = "no image";
		
		if(file.toString() != "Optional.empty") {
		String fileExtension = file.get().getContentType().split("/")[1];
		System.out.println(fileExtension);
		String newFileName = "PROD-" + date.getTime() + "." +  fileExtension;
		
		String fileName = StringUtils.cleanPath(newFileName);
		
		Path path = Paths.get(StringUtils.cleanPath(uploadPath)  + fileName);
		
		try {
			Files.copy(file.get().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/products/download/")
				.path(fileName).toUriString();
		}
		
		product.setImage(fileDownloadUri);
		product.setStock(product.getStockGudang());
		productRepo.save(product);
		
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
	
	@GetMapping("/report/{minPrice}/{maxPrice}")
	public Iterable<Product> findProductforCharts
		(
			@PathVariable double minPrice, 
			@PathVariable double maxPrice, 
			@RequestParam String productName,
			@RequestParam String sortList
		){
		if (maxPrice == 0) {
			maxPrice = 9999999;
		}
			if (sortList.equals("asc")) {
				return productRepo.findProductforChartAsc(minPrice, maxPrice, productName);
			}else {
				return productRepo.findProductforChartDesc(minPrice, maxPrice, productName);
			}
		}
	
	@GetMapping("/report/{minPrice}/category/{maxPrice}")
		public Iterable<Product> findProductWithCategoryforCharts
			(
			@PathVariable double minPrice, 
			@PathVariable double maxPrice, 
			@RequestParam String productName, 
			@RequestParam String nama, 
			@RequestParam String sortList
			){
		
		if (maxPrice == 0) {
			maxPrice = 9999999;
		}
			if (sortList.equals("asc")) {
				return productRepo.findProductCategoryforChartAsc(minPrice, maxPrice, productName, nama);
			}else {
				return productRepo.findProductCategoryforChartDesc(minPrice, maxPrice, productName, nama);
			}
		}
	
//	@GetMapping("/pages")
//	 public Product getAllProduct(Pageable pageable) {
//		Product findProduct = (Product) pagingRepo.findAll(pageable);
//	    return productRepo.save(findProduct);
//	 }
//	
	
}



