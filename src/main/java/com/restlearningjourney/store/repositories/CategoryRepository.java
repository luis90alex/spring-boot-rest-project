package com.restlearningjourney.store.repositories;

import com.restlearningjourney.store.entities.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Byte> {
}