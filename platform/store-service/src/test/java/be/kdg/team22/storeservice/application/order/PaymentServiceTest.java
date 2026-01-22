package be.kdg.team22.storeservice.application.order;

import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.application.catalog.StoreService;
import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.domain.order.*;
import be.kdg.team22.storeservice.domain.order.exceptions.OrderNotFoundException;
import be.kdg.team22.storeservice.domain.order.exceptions.PaymentIncompleteException;
import be.kdg.team22.storeservice.domain.payment.PaymentProvider;
import be.kdg.team22.storeservice.infrastructure.messaging.OrderEventPublisher;
import com.mollie.mollie.models.components.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentProvider paymentProvider;
    private OrderRepository orderRepo;
    private CartService cartService;
    private StoreService storeService;
    private OrderEventPublisher eventPublisher;

    private PaymentService service;

    private UUID orderId;
    private UUID gameId;

    private Order order;

    @BeforeEach
    void setup() {
        paymentProvider = mock(PaymentProvider.class);
        orderRepo = mock(OrderRepository.class);
        cartService = mock(CartService.class);
        storeService = mock(StoreService.class);
        eventPublisher = mock(OrderEventPublisher.class);

        service = new PaymentService(paymentProvider, orderRepo, cartService, storeService, eventPublisher);

        orderId = UUID.randomUUID();
        gameId = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        order = new Order(
                new OrderId(orderId),
                List.of(new OrderItem(GameId.from(gameId), BigDecimal.TEN)),
                OrderStatus.PENDING_PAYMENT,
                new be.kdg.team22.storeservice.domain.cart.UserId(user),
                "PAY123"
        );
    }

    @Test
    @DisplayName("processWebhook → throws OrderNotFound when paymentId is unknown")
    void processWebhook_orderNotFound() {
        when(orderRepo.findByPaymentId("X")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.processWebhook("X"))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("processWebhook → paid → updates order, clears cart, records purchases, saves")
    void processWebhook_paid() {
        when(orderRepo.findByPaymentId("PAY123")).thenReturn(Optional.of(order));
        when(paymentProvider.getPaymentStatus("PAY123")).thenReturn(PaymentStatus.PAID);

        service.processWebhook("PAY123");

        assertThat(order.status()).isEqualTo(OrderStatus.PAID);

        verify(cartService).clear(order.userId());
        verify(storeService).recordPurchase(GameId.from(gameId));

        verify(orderRepo).save(order);
    }

    @Test
    @DisplayName("processWebhook → canceled → updates status + saves")
    void processWebhook_canceled() {
        when(orderRepo.findByPaymentId("PAY123")).thenReturn(Optional.of(order));
        when(paymentProvider.getPaymentStatus("PAY123")).thenReturn(PaymentStatus.CANCELED);

        service.processWebhook("PAY123");

        assertThat(order.status()).isEqualTo(OrderStatus.CANCELED);

        verify(orderRepo).save(order);
        verifyNoInteractions(cartService, storeService);
    }

    @Test
    @DisplayName("processWebhook → expired → updates status + saves")
    void processWebhook_expired() {
        when(orderRepo.findByPaymentId("PAY123")).thenReturn(Optional.of(order));
        when(paymentProvider.getPaymentStatus("PAY123")).thenReturn(PaymentStatus.EXPIRED);

        service.processWebhook("PAY123");

        assertThat(order.status()).isEqualTo(OrderStatus.EXPIRED);

        verify(orderRepo).save(order);
        verifyNoInteractions(cartService, storeService);
    }

    @Test
    @DisplayName("processWebhook → failed → updates status + saves")
    void processWebhook_failed() {
        when(orderRepo.findByPaymentId("PAY123")).thenReturn(Optional.of(order));
        when(paymentProvider.getPaymentStatus("PAY123")).thenReturn(PaymentStatus.FAILED);

        service.processWebhook("PAY123");

        assertThat(order.status()).isEqualTo(OrderStatus.FAILED);

        verify(orderRepo).save(order);
        verifyNoInteractions(cartService, storeService);
    }

    @Test
    @DisplayName("verifyPayment → throws when order not found")
    void verifyPayment_notFound() {
        when(orderRepo.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.verifyPayment(new OrderId(orderId)))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("verifyPayment → throws PaymentIncompleteException when status != PAID")
    void verifyPayment_incomplete() {
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentProvider.getPaymentStatus("PAY123")).thenReturn(PaymentStatus.OPEN);

        assertThatThrownBy(() -> service.verifyPayment(new OrderId(orderId)))
                .isInstanceOf(PaymentIncompleteException.class);
    }

    @Test
    @DisplayName("verifyPayment → PAID → updates order, clears cart, records purchase, saves, returns order")
    void verifyPayment_success() {
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentProvider.getPaymentStatus("PAY123")).thenReturn(PaymentStatus.PAID);

        Order result = service.verifyPayment(new OrderId(orderId));

        assertThat(result.status()).isEqualTo(OrderStatus.PAID);

        verify(cartService).clear(order.userId());
        verify(storeService).recordPurchase(GameId.from(gameId));

        verify(orderRepo).save(order);
    }
}