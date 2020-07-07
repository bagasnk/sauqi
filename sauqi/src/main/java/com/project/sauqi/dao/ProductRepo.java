package com.project.sauqi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.sauqi.entity.Product;

public interface ProductRepo extends JpaRepository <Product,Integer>{
	//public Product findByCategory(String category);
	
	@Query(value = "SELECT * FROM Product WHERE category = ?", nativeQuery = true)
	public Iterable<Product> findByCategory(String category);
	
	@Query(value = "SELECT * FROM Product WHERE price <= ?", nativeQuery = true)
	public Iterable<Product> findProductByMaxPrice(double maxPrice);
	
	@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by product_name asc",nativeQuery = true)
	public Iterable<Product> findProductByPriceOrderByProductNameAsc(double minPrice, double maxPrice, String productName);

	@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by product_name desc",nativeQuery = true)
	public Iterable<Product> findProductByPriceOrderByProductNameDesc(double minPrice, double maxPrice, String productName);

	@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by price asc",nativeQuery = true)
	public Iterable<Product> findProductByPriceOrderByPriceAsc(double minPrice, double maxPrice, String productName);
	
	@Query(value = "select * from product where price >=?1 and price <= ?2 and product_name like %?3% order by price desc",nativeQuery = true)
	public Iterable<Product> findProductByPriceOrderByPriceDesc(double minPrice, double maxPrice, String productName);
	
	//Product with Categories FILTER AND SORT

	@Query(value = "select * from prodcat pc join product p on p.id = pc.products_id join categories c on c.id = pc.categories_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.nama=?4 order by product_name asc", nativeQuery = true)
	public Iterable<Product> findProductCategoryByPriceOrderByProductNameAsc(double minPrice, double maxPrice, String productName, String nama);

	@Query(value = "select * from prodcat pc join product p on p.id = pc.products_id join categories c on c.id = pc.categories_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.nama=?4 order by product_name desc", nativeQuery = true)
	public Iterable<Product> findProductCategoryByPriceOrderByProductNameDesc(double minPrice, double maxPrice, String productName, String nama);

	@Query(value = "select * from prodcat pc join product p on p.id = pc.products_id join categories c on c.id = pc.categories_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.nama=?4 order by price asc", nativeQuery = true)
	public Iterable<Product> findProductCategoryByPriceOrderByPriceAsc(double minPrice, double maxPrice, String productName, String nama);

	@Query(value = "select * from prodcat pc join product p on p.id = pc.products_id join categories c on c.id = pc.categories_id where p.price>=?1 and p.price<= ?2 and p.product_name like %?3% and c.nama=?4 order by price desc", nativeQuery = true)
	public Iterable<Product> findProductCategoryByPriceOrderByPriceDesc(double minPrice, double maxPrice, String productName, String nama);
}
