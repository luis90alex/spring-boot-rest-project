package com.restlearningjourney.store.repositories;

import com.restlearningjourney.store.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}