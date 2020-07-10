package com.project.sauqi.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.sauqi.entity.Paket;

public interface PaketRepo extends JpaRepository<Paket, Integer> {
	public Optional<Paket> findByPaketName(String paketName);
}
