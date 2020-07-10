package com.project.sauqi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.sauqi.entity.TransactionDetails;

public interface TransactionDetailsRepo extends JpaRepository <TransactionDetails, Integer>{

}
