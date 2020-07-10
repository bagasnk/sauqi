package com.project.sauqi.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Paket {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String paketName;
	private double paketPrice;
	private String imagePaket;
	private int stock;
	private int isSold;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="paket",cascade = CascadeType.ALL)
	private List<Product> products;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="paket",cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Cart> carts;

	public int getId() {
		return id;
	}

	public String getPaketName() {
		return paketName;
	}

	public double getPaketPrice() {
		return paketPrice;
	}

	public String getImagePaket() {
		return imagePaket;
	}

	public int getStock() {
		return stock;
	}

	public int getIsSold() {
		return isSold;
	}

	public List<Product> getProducts() {
		return products;
	}

	public List<Cart> getCarts() {
		return carts;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPaketName(String paketName) {
		this.paketName = paketName;
	}

	public void setPaketPrice(double paketPrice) {
		this.paketPrice = paketPrice;
	}

	public void setImagePaket(String imagePaket) {
		this.imagePaket = imagePaket;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public void setIsSold(int isSold) {
		this.isSold = isSold;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public void setCarts(List<Cart> carts) {
		this.carts = carts;
	}
	
	

}
