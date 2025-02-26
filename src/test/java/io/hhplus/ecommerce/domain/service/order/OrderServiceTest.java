package io.hhplus.ecommerce.domain.service.order;

import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.common.exception.OrderFailedException;
import io.hhplus.ecommerce.domain.entity.order.Order;
import io.hhplus.ecommerce.domain.entity.order.OrderDetail;
import io.hhplus.ecommerce.infra.order.OrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderJpaRepository orderJpaRepository;

    @InjectMocks
    private OrderService orderService;

    private UserDto userDto;
    private ProductDto product1;
    private ProductDto product2;
    private List<OrderDetailDto> orderDetailDtoList;
    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // UserDto 초기화
        userDto = UserDto.builder()
                .userId(1L)
                .name("testUser")
                .point(BigDecimal.valueOf(10000))
                .build();

        // ProductDto 초기화
        product1 = ProductDto.builder()
                .productId(1L)
                .name("Product 1")
                .price(BigDecimal.valueOf(1000))
                .build();

        product2 = ProductDto.builder()
                .productId(2L)
                .name("Product 2")
                .price(BigDecimal.valueOf(2000))
                .build();

        // OrderDetailDto 리스트 초기화
        OrderDetailDto orderDetail1 = OrderDetailDto.builder()
                .productDto(product1)
                .quantity(2)
                .build();

        OrderDetailDto orderDetail2 = OrderDetailDto.builder()
                .productDto(product2)
                .quantity(3)
                .build();

        orderDetailDtoList = Arrays.asList(orderDetail1, orderDetail2);

        // Order 객체 초기화
        order = Order.builder()
                .user(userDto.toEntity())
                .orderDate(LocalDateTime.now())
                .orderDetails(List.of(
                        OrderDetail.builder().product(product1.toEntity()).quantity(2).build(),
                        OrderDetail.builder().product(product2.toEntity()).quantity(3).build()
                ))
                .build();
    }

    @Test
    @DisplayName("주문 생성 및 TotalPoint 차감 확인 테스트")
    void orderPaymentSuccess() {
        // given
        BigDecimal expectedTotalPrice = BigDecimal.valueOf(1000 * 2 + 2000 * 3);  // 8000

        when(orderJpaRepository.save(any(Order.class))).thenReturn(order);

        // when: orderPayment 메서드를 호출
        OrderDto result = orderService.orderPayment(userDto, orderDetailDtoList);

        // then: 총 가격이 올바르게 계산되었는지 확인
        assertThat(result.getTotalPrice()).isEqualTo(expectedTotalPrice);

        // 저장된 Order 객체를 확인하기 위해 ArgumentCaptor 사용
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderJpaRepository, times(1)).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        // 저장된 Order 객체 검증
        assertThat(savedOrder.getUser().getUserId()).isEqualTo(userDto.getUserId());
        assertThat(savedOrder.getOrderDetails()).hasSize(2);  // 두 개의 상세 주문

        // 주문 상세 제품별 수량 검증
        assertThat(savedOrder.getOrderDetails().get(0).getProduct().getProductId()).isEqualTo(product1.getProductId());
        assertThat(savedOrder.getOrderDetails().get(0).getQuantity()).isEqualTo(2);

        assertThat(savedOrder.getOrderDetails().get(1).getProduct().getProductId()).isEqualTo(product2.getProductId());
        assertThat(savedOrder.getOrderDetails().get(1).getQuantity()).isEqualTo(3);
        
        // 반환된 OrderDto의 필드 검증
        assertThat(result.getOrderId()).isEqualTo(savedOrder.getOrderId());
        assertThat(result.getUserId()).isEqualTo(userDto.getUserId());
        assertThat(result.getOrderDetails()).hasSize(2);  // 주문 상세의 개수가 일치하는지
    }

    @Test
    @DisplayName("DataIntegrityViolationException 확인 테스트")
    void DataIntegrityViolationException_Test() {
        when(orderJpaRepository.save(any(Order.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(OrderFailedException.class, () -> orderService.orderPayment(userDto, orderDetailDtoList));
    }

    @Test
    @DisplayName("Exception 테스트")
    void Test() {
        when(orderJpaRepository.save(any(Order.class))).thenThrow(NullPointerException.class);
        // then
        assertThrows(OrderFailedException.class, () -> orderService.orderPayment(userDto, orderDetailDtoList));
    }
}