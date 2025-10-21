package com.restlearningjourney.store.repositories;

import com.restlearningjourney.store.entities.Order;
import com.restlearningjourney.store.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "select o from Order o where o.customer.id = :customerId")
    List<Order> findOrdersByCustomerId(@Param("customerId") Long customerId);
}
