package com.restlearningjourney.store.orders;

import com.restlearningjourney.store.auth.AuthService;
import com.restlearningjourney.store.users.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void givenCurrentUser_whenGetAllOrders_thenReturnOrderDtoList() {
        // given: a logged-in user with some orders
        User user = new User();
        user.setId(1L);
        when(authService.getCurrentUser()).thenReturn(user);

        Order order1 = mock(Order.class);
        Order order2 = mock(Order.class);

        when(orderRepository.getOrdersByCustomer(user.getId()))
                .thenReturn(List.of(order1, order2));

        OrderDto dto1 = new OrderDto();
        OrderDto dto2 = new OrderDto();
        when(orderMapper.fromOrderToDto(order1)).thenReturn(dto1);
        when(orderMapper.fromOrderToDto(order2)).thenReturn(dto2);

        // when: fetching all orders
        List<OrderDto> result = orderService.getAllOrders();

        // then: list contains all mapped DTOs
        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));

        verify(authService).getCurrentUser();
        verify(orderRepository).getOrdersByCustomer(user.getId());
        verify(orderMapper).fromOrderToDto(order1);
        verify(orderMapper).fromOrderToDto(order2);
    }

    @Test
    void givenOrderIdNotFound_whenGetOrder_thenThrowOrderNotFoundException() {
        // given: repository returns empty
        Long orderId = 42L;
        when(orderRepository.getOrderWithItems(orderId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId));

        verify(orderRepository).getOrderWithItems(orderId);
        verifyNoInteractions(authService, orderMapper);
    }

    @Test
    void givenOrderNotBelongingToUser_whenGetOrder_thenThrowAccessDeniedException() {
        // given: order exists but belongs to another user
        Long orderId = 1L;
        Order order = mock(Order.class);
        when(orderRepository.getOrderWithItems(orderId)).thenReturn(Optional.of(order));

        User currentUser = new User();
        currentUser.setId(1L);
        when(authService.getCurrentUser()).thenReturn(currentUser);

        when(order.isPlacedBy(currentUser)).thenReturn(false);

        // when & then
        assertThrows(AccessDeniedException.class, () -> orderService.getOrder(orderId));

        verify(orderRepository).getOrderWithItems(orderId);
        verify(authService).getCurrentUser();
        verify(order).isPlacedBy(currentUser);
        verifyNoInteractions(orderMapper);
    }

    @Test
    void givenOrderBelongingToUser_whenGetOrder_thenReturnOrderDto() {
        // given: order exists and belongs to current user
        Long orderId = 1L;
        Order order = mock(Order.class);
        when(orderRepository.getOrderWithItems(orderId)).thenReturn(Optional.of(order));

        User currentUser = new User();
        currentUser.setId(1L);
        when(authService.getCurrentUser()).thenReturn(currentUser);

        when(order.isPlacedBy(currentUser)).thenReturn(true);

        OrderDto dto = new OrderDto();
        when(orderMapper.fromOrderToDto(order)).thenReturn(dto);

        // when: fetching the order
        OrderDto result = orderService.getOrder(orderId);

        // then: should return the mapped DTO
        assertNotNull(result);
        assertSame(dto, result);

        verify(orderRepository).getOrderWithItems(orderId);
        verify(authService).getCurrentUser();
        verify(order).isPlacedBy(currentUser);
        verify(orderMapper).fromOrderToDto(order);
    }
}
