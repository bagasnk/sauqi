package com.project.sauqi.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.project.sauqi.entity.Product;

public interface PagingRepo extends PagingAndSortingRepository<Product, Integer>{

}
