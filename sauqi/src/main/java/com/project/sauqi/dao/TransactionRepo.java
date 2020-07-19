package com.project.sauqi.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.project.sauqi.entity.Transaction;

public interface TransactionRepo extends JpaRepository <Transaction, Integer>, PagingAndSortingRepository<Transaction, Integer>{
	
	@Query(value = "select * from transaction where users_id = ?1 and status = ?2",nativeQuery = true)
	public Iterable<Transaction> findTransaksiByUserIdAndStatus(int id, String status);
	
	@Query(value = "select * from transaction where status = ?1",nativeQuery = true)
	public Page<Transaction> findTransaksiByStatus(String status,Pageable pageable);
	
//	@Query(value = "Select email from user where users_id = ?1",nativeQuery = true)
//	public Iterable<Transaction> findEmailById(int id);

}
