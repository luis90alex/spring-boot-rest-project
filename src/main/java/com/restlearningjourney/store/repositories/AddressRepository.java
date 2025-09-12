package com.restlearningjourney.store.repositories;

import com.restlearningjourney.store.entities.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}