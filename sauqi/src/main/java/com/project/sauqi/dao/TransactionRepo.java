package com.project.sauqi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.sauqi.entity.Transaction;

public interface TransactionRepo extends JpaRepository <Transaction, Integer>{
	

}
