package com.restlearningjourney.store.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "date_created")
    private LocalDate dateCreated;

    @OneToMany(mappedBy = "cart",  cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.setCart(null);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "dateCreated=" + dateCreated +
                ", id=" + id +
                '}';
    }
}
