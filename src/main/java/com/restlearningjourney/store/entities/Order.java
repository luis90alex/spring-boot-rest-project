package com.restlearningjourney.store.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)//store it as string
    private PaymentStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @OneToMany(mappedBy = "order",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<OrderItem> items = new HashSet<>();


    public Order() {
    }

    public Order(Long id, PaymentStatus status, LocalDateTime createdAt, BigDecimal totalPrice, User customer) {
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
        this.totalPrice = totalPrice;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    public void setItems(Set<OrderItem> orderItems) {
        this.items = orderItems;
    }

    public static Order fromCart(Cart cart, User customer){
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(PaymentStatus.PENDING);
        order.setTotalPrice(cart.getTotalPrice());

        customer.getOrders().add(order);

        cart.getItems().forEach(item -> {
            OrderItem orderItem = new OrderItem(order, item.getProduct(), item.getQuantity());
            System.out.println(orderItem);
            order.items.add(orderItem);
        });
        return order;
    }

    public boolean isPlacedBy(User customer){
        return this.customer.equals(customer);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", totalPrice=" + totalPrice +
                //", customer=" + customer.getId() +
                ", orderItems=" + items +
                '}';
    }
}
