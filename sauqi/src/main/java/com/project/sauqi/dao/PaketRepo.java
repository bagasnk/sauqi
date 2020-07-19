package com.project.sauqi.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.project.sauqi.entity.Paket;

public interface PaketRepo extends JpaRepository<Paket, Integer>,PagingAndSortingRepository<Paket, Integer> {
	public Optional<Paket> findByPaketName(String paketName);
	
	@Query(value = "select * from paket where paket_price >=?1 and paket_price <= ?2 and paket_name like %?3% order by paket_name asc",nativeQuery = true)
	public Page<Paket> findPaketByPriceOrderByPaketNameAsc(double minPrice, double maxPrice, String paketName, Pageable pageable);

	@Query(value = "select * from paket where paket_price >=?1 and paket_price <= ?2 and paket_name like %?3% order by paket_name desc",nativeQuery = true)
	public Page<Paket> findPaketByPriceOrderByPaketNameDesc(double minPrice, double maxPrice, String paketName,Pageable pageable);

	@Query(value = "select * from paket where paket_price >=?1 and paket_price <= ?2 and paket_name like %?3% order by paket_price asc",nativeQuery = true)
	public Page<Paket> findPaketByPriceOrderByPriceAsc(double minPrice, double maxPrice, String paketName,Pageable pageable);
	
	@Query(value = "select * from paket where paket_price >=?1 and paket_price <= ?2 and paket_name like %?3% order by paket_price desc",nativeQuery = true)
	public Page<Paket> findPaketByPriceOrderByPriceDesc(double minPrice, double maxPrice, String paketName,Pageable pageable);
	
}
