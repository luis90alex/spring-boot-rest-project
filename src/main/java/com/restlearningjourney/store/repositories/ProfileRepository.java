package com.restlearningjourney.store.repositories;

import com.restlearningjourney.store.entities.Profile;
import org.springframework.data.repository.CrudRepository;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}