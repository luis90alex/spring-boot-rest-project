package com.restlearningjourney.store.repositories;

import com.restlearningjourney.store.entities.CartItem;
import com.restlearningjourney.store.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemsRepository extends JpaRepository<CartItem, Long> {

    @Query(value = "select ci from CartItem ci where ci.cart.id = :cartId and ci.product.id = :productId")
    Optional<CartItem> findByCartIdAndProductId(@Param("cartId") UUID cartId, @Param("productId") Long productId);

}
