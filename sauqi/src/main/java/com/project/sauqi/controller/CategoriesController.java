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
import org.springframework.web.bind.annotation.RestController;

import com.project.sauqi.dao.CategoriesRepo;
import com.project.sauqi.dao.ProductRepo;
import com.project.sauqi.entity.Categories;
import com.project.sauqi.entity.Product;

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoriesController {
	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
	private CategoriesRepo categoriesRepo;
	
	@PostMapping
	public Categories addCategories(@RequestBody Categories categories) {
		return categoriesRepo.save(categories);
	}
	
	@GetMapping
	public Iterable<Categories> getCategories() {
		return categoriesRepo.findAll();
	}
	
	@GetMapping("/{categoriesId}/products")
	public List<Product> getProductOfCategories(@PathVariable int categoriesId) {
		Categories findCategories = categoriesRepo.findById(categoriesId).get();
		
		return findCategories.getProduct();
	}
	
	@DeleteMapping("/{categoriesId}")
	public void deleteCategories(@PathVariable int categoriesId) {
		Categories findCategories = categoriesRepo.findById(categoriesId).get();
		
		findCategories.getProduct().forEach(product -> {
			List<Categories> productCategories = product.getCategories();
			productCategories.remove(findCategories);
			productRepo.save(product);
		});
		findCategories.setProduct(null);
		categoriesRepo.deleteById(categoriesId);
	}
	
	@PutMapping("/edit")
	public Categories editCategories(@RequestBody Categories categories) {
		Categories findCategories = categoriesRepo.findById(categories.getId()).get();
		categories.setProduct(findCategories.getProduct());
		return categoriesRepo.save(categories);
	}
}
