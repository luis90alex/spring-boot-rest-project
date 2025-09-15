package com.restlearningjourney.store.repositories;

import com.restlearningjourney.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
