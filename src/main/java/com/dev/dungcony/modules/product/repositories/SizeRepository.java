package com.dev.dungcony.modules.product.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.dungcony.modules.product.entities.Size;

public interface SizeRepository extends JpaRepository<Size, Integer> {

}
