package com.restlearningjourney.store.integration;

import com.restlearningjourney.store.auth.AuthService;
import com.restlearningjourney.store.auth.LoginRequest;
import com.restlearningjourney.store.auth.LoginResponse;
import com.restlearningjourney.store.carts.CartDto;
import com.restlearningjourney.store.carts.CartService;
import com.restlearningjourney.store.orders.Order;
import com.restlearningjourney.store.orders.OrderRepository;
import com.restlearningjourney.store.payments.*;
import com.restlearningjourney.store.products.Category;
import com.restlearningjourney.store.products.CategoryRepository;
import com.restlearningjourney.store.products.ProductDto;
import com.restlearningjourney.store.products.ProductService;
import com.restlearningjourney.store.users.RegisterUserRequest;
import com.restlearningjourney.store.users.User;
import com.restlearningjourney.store.users.UserDto;
import com.restlearningjourney.store.users.UserRepository;
import com.restlearningjourney.store.users.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import(CheckoutIntegrationTest.TestConfig.class)
@Transactional
class CheckoutIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentGateway paymentGateway; // replaced by TestConfig

    @TestConfiguration
    static class TestConfig {

        @Bean
        public PaymentGateway paymentGateway() {
            return new PaymentGateway() {
                @Override
                public CheckoutSession createCheckoutSession(Order order) {
                    return new CheckoutSession("https://checkout.mock/integration");
                }

                @Override
                public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
                    return Optional.empty();
                }
            };
        }
    }

    @Test
    void givenRegisteredUser_whenLoginCreateCartAddItemsAndCheckout_thenOrderCreatedAndCartCleared() {
        // --- Step 1: register a real user ---
        RegisterUserRequest reg = new RegisterUserRequest();
        reg.setEmail("integration@test.com");
        reg.setName("Integration");
        reg.setPassword("plainpassword");
        UserDto created = userService.registerUser(reg);

        // --- Step 2: login the user and populate SecurityContext ---
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(created.getEmail());
        loginRequest.setPassword(reg.getPassword());
        LoginResponse loginResponse = authService.login(loginRequest);

        // Set SecurityContext manually so getCurrentUser() works
        Long userId = created.getId();
        User persistedUser = userRepository.findById(userId).orElseThrow();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, // principal matches getCurrentUser()
                null,
                Collections.emptyList() // empty or roles
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // --- Step 3: create category ---
        Category category = new Category();
        category.setName("Electronics");
        categoryRepository.save(category);

        // --- Step 4: create products ---
        ProductDto p1 = new ProductDto();
        p1.setName("Product One");
        p1.setDescription("Description One");
        p1.setPrice(BigDecimal.ONE);
        p1.setCategoryId(category.getId());
        productService.createProduct(p1);

        ProductDto p2 = new ProductDto();
        p2.setName("Product Two");
        p2.setDescription("Description Two");
        p2.setPrice(BigDecimal.ONE);
        p2.setCategoryId(category.getId());
        productService.createProduct(p2);

        // --- Step 5: create cart and add items ---
        CartDto cartDto = cartService.createCart();
        UUID cartId = cartDto.getId();

        cartService.addToCart(cartId, p1.getId());
        cartService.addToCart(cartId, p2.getId());

        // sanity check: cart contains items
        CartDto cartBefore = cartService.getCart(cartId);
        assertFalse(cartBefore.getItems().isEmpty(), "Cart must contain items before checkout");

        // --- Step 6: perform checkout ---
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setCartId(cartId);
        CheckoutResponse response = checkoutService.checkout(checkoutRequest);

        // --- Step 7: assertions ---
        assertNotNull(response);
        assertEquals("https://checkout.mock/integration", response.getCheckoutUrl());
        assertTrue(orderRepository.count() >= 1, "At least one order should be persisted after checkout");
    }
}
