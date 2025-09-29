package com.restlearningjourney.store.entities;


import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Builder.Default
    @Column(name = "quantity")
    private Integer quantity = 1;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @ToString.Exclude
    private Cart cart;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    private Product product;

}
