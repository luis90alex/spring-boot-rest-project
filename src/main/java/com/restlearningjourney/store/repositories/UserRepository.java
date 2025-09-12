package com.restlearningjourney.store.repositories;

import com.restlearningjourney.store.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
