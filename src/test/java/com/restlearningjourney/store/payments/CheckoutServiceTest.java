package com.restlearningjourney.store.payments;

import com.restlearningjourney.store.auth.AuthService;
import com.restlearningjourney.store.carts.*;
import com.restlearningjourney.store.orders.Order;
import com.restlearningjourney.store.orders.OrderRepository;
import com.restlearningjourney.store.orders.PaymentStatus;
import com.restlearningjourney.store.users.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartService cartService;

    @Mock
    private AuthService authService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    void givenCartNotFound_whenCheckout_thenThrowCartNotFoundException() {
        // given: cart does not exist
        UUID cartId = UUID.randomUUID();
        CheckoutRequest request = new CheckoutRequest();
        request.setCartId(cartId);

        when(cartRepository.getCartWithItems(cartId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CartNotFoundException.class, () -> checkoutService.checkout(request));

        verify(cartRepository).getCartWithItems(cartId);
        verifyNoInteractions(authService, orderRepository, cartService, paymentGateway);
    }

    @Test
    void givenEmptyCart_whenCheckout_thenThrowCartEmptyException() {
        // given: cart exists but is empty
        UUID cartId = UUID.randomUUID();
        CheckoutRequest request = new CheckoutRequest();
        request.setCartId(cartId);

        Cart cart = mock(Cart.class);
        when(cartRepository.getCartWithItems(cartId)).thenReturn(Optional.of(cart));
        when(cart.isEmpty()).thenReturn(true);

        // when & then
        assertThrows(CartEmptyException.class, () -> checkoutService.checkout(request));

        verify(cartRepository).getCartWithItems(cartId);
        verify(cart).isEmpty();
        verifyNoInteractions(authService, orderRepository, cartService, paymentGateway);
    }

    @Test
    void givenValidCart_whenCheckout_thenReturnCheckoutResponse() throws PaymentException {
        // given: valid cart with items
        UUID cartId = UUID.randomUUID();
        CheckoutRequest request = new CheckoutRequest();
        request.setCartId(cartId);

        Cart cart = mock(Cart.class);
        when(cartRepository.getCartWithItems(cartId)).thenReturn(Optional.of(cart));
        when(cart.isEmpty()).thenReturn(false);

        User user = new User();
        user.setId(1L);
        when(authService.getCurrentUser()).thenReturn(user);

        // Let the service create the Order via Order.fromCart(...) (do not mock the static factory)
        // Ensure repository.save returns the same Order instance passed in
        when(orderRepository.save(any(Order.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        CheckoutSession session = new CheckoutSession("https://checkout.url");
        when(paymentGateway.createCheckoutSession(any(Order.class))).thenReturn(session);
        when(cart.getId()).thenReturn(cartId);
        // when
        CheckoutResponse result = checkoutService.checkout(request);

        // then
        assertNotNull(result);
        assertEquals(session.getCheckoutUrl(), result.getCheckoutUrl());

        verify(cartRepository).getCartWithItems(cartId);
        verify(cart).isEmpty();
        verify(authService).getCurrentUser();
        verify(orderRepository).save(any(Order.class));
        verify(paymentGateway).createCheckoutSession(any(Order.class));
        verify(cartService).clearCart(cartId);
    }

    @Test
    void givenPaymentFails_whenCheckout_thenDeleteOrderAndThrow() throws PaymentException {
        // given: valid cart but payment fails
        UUID cartId = UUID.randomUUID();
        CheckoutRequest request = new CheckoutRequest();
        request.setCartId(cartId);

        Cart cart = mock(Cart.class);
        when(cartRepository.getCartWithItems(cartId)).thenReturn(Optional.of(cart));
        when(cart.isEmpty()).thenReturn(false);

        User user = new User();
        user.setId(1L);
        when(authService.getCurrentUser()).thenReturn(user);

        // Let the service create the Order via Order.fromCart(...) (do not mock the static factory)
        // Ensure repository.save returns the same Order instance passed in
        when(orderRepository.save(any(Order.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        // payment gateway will throw
        when(paymentGateway.createCheckoutSession(any(Order.class)))
                .thenThrow(new PaymentException("Payment failed"));

        // when & then
        PaymentException ex = assertThrows(PaymentException.class, () -> checkoutService.checkout(request));
        assertEquals("Payment failed", ex.getMessage());

        // then: verify interactions (use any(Order.class) because we didn't capture the exact instance)
        verify(orderRepository).save(any(Order.class));
        verify(paymentGateway).createCheckoutSession(any(Order.class));
        verify(orderRepository).delete(any(Order.class));
        verify(cartService, never()).clearCart(any());
    }

    @Test
    void givenWebhookEvent_whenHandleWebhook_thenUpdateOrderStatus() {
        // given
        WebhookRequest request = new WebhookRequest();
        PaymentResult paymentResult = new PaymentResult();
        paymentResult.setOrderId(1L);
        paymentResult.setPaymentStatus(PaymentStatus.PAID);

        when(paymentGateway.parseWebhookRequest(request)).thenReturn(Optional.of(paymentResult));

        Order order = mock(Order.class);
        when(orderRepository.findById(paymentResult.getOrderId())).thenReturn(Optional.of(order));

        // when
        checkoutService.handleWebhookEvent(request);

        // then
        verify(order).setStatus(PaymentStatus.PAID);
        verify(orderRepository).save(order);
        verify(paymentGateway).parseWebhookRequest(request);
    }

    @Test
    //Testing ifPresent
    void givenWebhookEventWithNoResult_whenHandleWebhook_thenDoNothing() {
        // given
        WebhookRequest request = new WebhookRequest();
        when(paymentGateway.parseWebhookRequest(request)).thenReturn(Optional.empty());

        // when
        checkoutService.handleWebhookEvent(request);

        // then
        verify(paymentGateway).parseWebhookRequest(request);
        verifyNoInteractions(orderRepository);
    }
}