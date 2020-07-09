package com.project.sauqi.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.project.sauqi.entity.Product;

public interface ProductRepo extends JpaRepository<Product,Integer>,PagingAndSortingRepository<Product, Integer>{
	//public Product findByCategory(String category);
	
	@Query(value = "SELECT * FROM Product WHERE category = ?", nativeQuery = true)
	public Iterable<Product> findByCategory(String category);
	
	@Query(value = "SELECT * FROM Product WHERE price <= ?", nativeQuery = true)
	public Iterable<Product> findProductByMaxPrice(double maxPrice);
	
	@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by product_name asc",nativeQuery = true)
	public Page<Product> findProductByPriceOrderByProductNameAsc(double minPrice, double maxPrice, String productName, Pageable pageable);

	@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by product_name desc",nativeQuery = true)
	public Page<Product> findProductByPriceOrderByProductNameDesc(double minPrice, double maxPrice, String productName,Pageable pageable);

	@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by price asc",nativeQuery = true)
	public Page<Product> findProductByPriceOrderByPriceAsc(double minPrice, double maxPrice, String productName,Pageable pageable);
	
	@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by price desc",nativeQuery = true)
	public Page<Product> findProductByPriceOrderByPriceDesc(double minPrice, double maxPrice, String productName,Pageable pageable);
	
	//Product with Categories FILTER AND SORT

	@Query(value = "select * from prodcat pc join product p on p.id = pc.products_id join categories c on c.id = pc.categories_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.nama=?4 order by product_name asc", nativeQuery = true)
	public Page<Product> findProductCategoryByPriceOrderByProductNameAsc(double minPrice, double maxPrice, String productName, String nama,Pageable pageable);

	@Query(value = "select * from prodcat pc join product p on p.id = pc.products_id join categories c on c.id = pc.categories_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.nama=?4 order by product_name desc", nativeQuery = true)
	public Page<Product> findProductCategoryByPriceOrderByProductNameDesc(double minPrice, double maxPrice, String productName, String nama,Pageable pageable);

	@Query(value = "select * from prodcat pc join product p on p.id = pc.products_id join categories c on c.id = pc.categories_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.nama=?4 order by price asc", nativeQuery = true)
	public Page<Product> findProductCategoryByPriceOrderByPriceAsc(double minPrice, double maxPrice, String productName, String nama,Pageable pageable);

	@Query(value = "select * from prodcat pc join product p on p.id = pc.products_id join categories c on c.id = pc.categories_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.nama=?4 order by price desc", nativeQuery = true)
	public Page<Product> findProductCategoryByPriceOrderByPriceDesc(double minPrice, double maxPrice, String productName, String nama,Pageable pageable);
}
