package com.project.sauqi.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.sauqi.entity.Cart;

public interface CartRepo extends JpaRepository <Cart, Integer> {

}
