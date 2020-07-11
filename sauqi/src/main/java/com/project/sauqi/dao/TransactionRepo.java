package com.project.sauqi.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.sauqi.entity.Product;
import com.project.sauqi.entity.Transaction;

public interface TransactionRepo extends JpaRepository <Transaction, Integer>{
	
	@Query(value = "select * from transaction where users_id = ?1 and status = ?2",nativeQuery = true)
	public Iterable<Transaction> findTransaksiByStatus(int id, String productName);

}
