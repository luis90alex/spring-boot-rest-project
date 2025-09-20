package com.restlearningjourney.store.repositories;

import com.restlearningjourney.store.entities.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "select p from Product p where p.category.id = :categoryId")
    List<Product> findProductsByCategoryId(@Param("categoryId") Byte categoryId);

    @EntityGraph(attributePaths = "category")
    @Query(value = "select p from Product p")
    List<Product> findAllIncludingCategories();
}