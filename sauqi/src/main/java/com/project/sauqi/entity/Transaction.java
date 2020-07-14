package com.project.sauqi.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int totalPrice;
	private String BuktiTrf;
	private String status;
	private String tanggalBeli;
	private String tanggalAcc;
	private String tanggalReject;
	private String jasaPengiriman;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "users_id")
	private User user;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "transaction", cascade = CascadeType.ALL)
	private List<TransactionDetails> transactionDetails;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getBuktiTrf() {
		return BuktiTrf;
	}

	public void setBuktiTrf(String buktiTrf) {
		BuktiTrf = buktiTrf;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTanggalBeli() {
		return tanggalBeli;
	}

	public void setTanggalBeli(String tanggalBeli) {
		this.tanggalBeli = tanggalBeli;
	}

	public String getTanggalAcc() {
		return tanggalAcc;
	}

	public void setTanggalAcc(String tanggalAcc) {
		this.tanggalAcc = tanggalAcc;
	}
	

	public String getTanggalReject() {
		return tanggalReject;
	}

	public void setTanggalReject(String tanggalReject) {
		this.tanggalReject = tanggalReject;
	}

	public String getJasaPengiriman() {
		return jasaPengiriman;
	}

	public void setJasaPengiriman(String jasaPengiriman) {
		this.jasaPengiriman = jasaPengiriman;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<TransactionDetails> getTransactionDetails() {
		return transactionDetails;
	}

	public void setTransactionDetails(List<TransactionDetails> transactionDetails) {
		this.transactionDetails = transactionDetails;
	}
	
	
	
	
}
	
	