package com.restlearningjourney.store.carts;

import com.restlearningjourney.store.products.Product;
import com.restlearningjourney.store.products.ProductNotFoundException;
import com.restlearningjourney.store.products.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void givenInvalidCartId_whenAddToCart_thenThrowCartNotFoundException() {
        // given: cart does not exist
        UUID cartId = UUID.randomUUID();
        when(cartRepository.getCartWithItems(cartId)).thenReturn(Optional.empty());

        // when + then: expect CartNotFoundException
        assertThrows(CartNotFoundException.class, () -> cartService.addToCart(cartId, 1L));

        verify(cartRepository).getCartWithItems(cartId);

        //productRepository not consulted
        verifyNoInteractions(productRepository);
    }

    @Test
    void givenCartExistsButProductMissing_whenAddToCart_thenThrowProductNotFoundException() {
        // given: cart exists but product does not
        UUID cartId = UUID.randomUUID();
        Cart cart = mock(Cart.class);

        when(cartRepository.getCartWithItems(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(5L)).thenReturn(Optional.empty());

        // when + then: expect ProductNotFoundException
        assertThrows(ProductNotFoundException.class, () -> cartService.addToCart(cartId, 5L));

        verify(cartRepository).getCartWithItems(cartId);
        verify(productRepository).findById(5L);
    }

    @Test
    void givenCartAndProductExist_whenAddToCart_thenReturnCartItemDto() {
        // given: valid cart and product
        UUID cartId = UUID.randomUUID();
        Cart cart = mock(Cart.class);
        Product product = new Product();
        product.setId(5L);

        CartItem cartItem = mock(CartItem.class);
        CartItemDto cartItemDto = new CartItemDto();

        when(cartRepository.getCartWithItems(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(cart.addItem(product)).thenReturn(cartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toDto(cartItem)).thenReturn(cartItemDto);

        // when: adding product to cart
        CartItemDto result = cartService.addToCart(cartId, 5L);

        // then: expect mapper result
        assertNotNull(result);
        assertSame(cartItemDto, result);

        verify(cartRepository).save(cart);
        verify(cartMapper).toDto(cartItem);
    }

    @Test
    void givenCartDoesNotExist_whenGetCart_thenThrowCartNotFoundException() {
        // given: cart id not found
        UUID cartId = UUID.randomUUID();
        when(cartRepository.getCartWithItems(cartId)).thenReturn(Optional.empty());

        // when + then: expect exception
        assertThrows(CartNotFoundException.class, () -> cartService.getCart(cartId));
    }

    @Test
    void givenCartExists_whenGetCart_thenReturnCartDto() {
        // given: cart exists and dto mapping is available
        UUID cartId = UUID.randomUUID();
        Cart cart = mock(Cart.class);
        CartDto dto = new CartDto();

        when(cartRepository.getCartWithItems(cartId)).thenReturn(Optional.of(cart));
        when(cartMapper.toDto(cart)).thenReturn(dto);

        // when: retrieving cart
        CartDto result = cartService.getCart(cartId);

        // then: expect mapped dto
        assertNotNull(result);
        assertSame(dto, result);

        verify(cartMapper).toDto(cart);
    }
}
