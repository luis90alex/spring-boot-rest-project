package com.restlearningjourney.store.entities;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "date_created", insertable = false, updatable = false)
    private LocalDate dateCreated;

    @OneToMany(mappedBy = "cart",  cascade = {CascadeType.MERGE},
            orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<CartItem> items = new LinkedHashSet<>();

    @Override
    public String toString() {
        return "Cart{" +
                "dateCreated=" + dateCreated +
                ", id=" + id +
                ", items =" + items +
                '}';
    }

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(item -> item.getTotalPrice())
                .reduce(BigDecimal.ZERO, (a,b) -> a.add(b));
    }

    public CartItem getItem(Long productId) {
        return items.stream().filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(null);
    }

    public CartItem addItem(Product product) {
        var cartItem = getItem(product.getId());
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setCart(this);
            items.add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() +1);
        }
        return cartItem;
    }

    public void removeItem(Long productId) {
        var cartItem = getItem(productId);
        if (cartItem != null) {
            items.remove(cartItem);
            cartItem.setCart(null);
        }
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
