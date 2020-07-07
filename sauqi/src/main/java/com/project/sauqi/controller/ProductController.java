package com.project.sauqi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.sauqi.dao.CategoriesRepo;
import com.project.sauqi.dao.ProductRepo;
import com.project.sauqi.entity.Categories;
import com.project.sauqi.entity.Product;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {

	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
	private CategoriesRepo categoriesRepo;
	
	
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
	
	@PostMapping("/addProduct")
	public Product addProduct(@RequestBody Product product) {
		return productRepo.save(product);
	}
	
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
	
	@PutMapping("/edit")
	public Product editProduct(@RequestBody Product product) {
		Product findProduct= productRepo.findById(product.getId()).get();
		product.setCategories(findProduct.getCategories());
		return productRepo.save(product);
	}
	
	@GetMapping("/{minPrice}/{maxPrice}/{orderBy}/{sortList}")
	public Iterable<Product> findProductByPrice
		(
			@PathVariable double minPrice, 
			@PathVariable double maxPrice, 
			@RequestParam String productName,
			@PathVariable String orderBy, 
			@PathVariable String sortList
		){
		if (maxPrice == 0) {
			maxPrice = 9999999;
		}
			if (orderBy.equals("productName") && sortList.equals("asc")) {
				return productRepo.findProductByPriceOrderByProductNameAsc(minPrice, maxPrice, productName);
			}else if (orderBy.equals("productName") && sortList.equals("desc")) {
				return productRepo.findProductByPriceOrderByProductNameDesc(minPrice, maxPrice, productName);
			}else if (orderBy.equals("price") && sortList.equals("asc")) {
				return productRepo.findProductByPriceOrderByPriceAsc(minPrice, maxPrice, productName);
			}else{
				return productRepo.findProductByPriceOrderByPriceDesc(minPrice, maxPrice, productName);
			}
		}
	
	@GetMapping("/{minPrice}/category/{maxPrice}/{orderBy}/{sortList}")
	public Iterable<Product> findProductWithFilter
			(
			@PathVariable double minPrice, 
			@PathVariable double maxPrice, 
			@RequestParam String productName, 
			@RequestParam String nama, 
			@PathVariable String orderBy, 
			@PathVariable String sortList
			){
		
		if (maxPrice == 0) {
			maxPrice = 9999999;
		}
			if (orderBy.equals("productName") && sortList.equals("asc")) {
				return productRepo.findProductCategoryByPriceOrderByProductNameAsc(minPrice, maxPrice, productName, nama);
			}
			else if (orderBy.equals("productName") && sortList.equals("desc")) {
				return productRepo.findProductCategoryByPriceOrderByProductNameDesc(minPrice, maxPrice, productName, nama);
			}
			else if (orderBy.equals("price") && sortList.equals("asc")) {
				return productRepo.findProductCategoryByPriceOrderByPriceAsc(minPrice, maxPrice, productName, nama);
			}else{
				return productRepo.findProductCategoryByPriceOrderByPriceDesc(minPrice, maxPrice, productName, nama);
			}
		}
}



