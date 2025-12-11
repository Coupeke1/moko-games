package be.kdg.team22.storeservice.application.order;

import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import be.kdg.team22.storeservice.domain.cart.CartItem;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.catalog.Entry;
import be.kdg.team22.storeservice.domain.catalog.EntryRepository;
import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.domain.order.*;
import be.kdg.team22.storeservice.domain.order.exceptions.OrderEmptyException;
import be.kdg.team22.storeservice.domain.order.exceptions.OrderNotFoundException;
import be.kdg.team22.storeservice.domain.order.exceptions.OrderNotOwnedException;
import be.kdg.team22.storeservice.domain.payment.Payment;
import be.kdg.team22.storeservice.domain.payment.PaymentProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private final UUID USER = UUID.randomUUID();
    private final UserId userId = new UserId(USER);
    private final UUID GAME1 = UUID.randomUUID();

    private OrderRepository repo;
    private CartService cartService;
    private EntryRepository catalog;
    private PaymentProvider paymentProvider;
    private OrderService service;

    @BeforeEach
    void setup() {
        repo = mock(OrderRepository.class);
        cartService = mock(CartService.class);
        catalog = mock(EntryRepository.class);
        paymentProvider = mock(PaymentProvider.class);

        service = new OrderService(repo, cartService, catalog, paymentProvider);
    }

    @Test
    @DisplayName("createOrder → throws OrderEmptyException when cart is empty")
    void createOrder_emptyCart() {
        Cart empty = new Cart(CartId.create(), UserId.from(USER));

        when(cartService.get(userId)).thenReturn(empty);

        assertThatThrownBy(() -> service.createOrder(userId)).isInstanceOf(OrderEmptyException.class);
    }

    @Test
    @DisplayName("createOrder → throws GameNotFoundException when catalog has no entry")
    void createOrder_gameNotFound() {
        Cart cart = new Cart(CartId.create(), UserId.from(USER), Set.of(new CartItem(GameId.from(GAME1))));

        when(cartService.get(userId)).thenReturn(cart);
        when(catalog.findById(GAME1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createOrder(userId)).isInstanceOf(GameNotFoundException.class);
    }

    @Test
    @DisplayName("createOrder → creates valid order with catalog prices and saves to repo")
    void createOrder_success() {
        Cart cart = new Cart(CartId.create(), UserId.from(USER), Set.of(new CartItem(GameId.from(GAME1))));

        when(cartService.get(userId)).thenReturn(cart);

        Entry entry = new Entry(GameId.from(GAME1), BigDecimal.valueOf(19.99), null, Collections.emptyList());
        when(catalog.findById(GAME1)).thenReturn(Optional.of(entry));

        final Order[] savedOrder = new Order[1];
        doAnswer(inv -> {
            savedOrder[0] = inv.getArgument(0);
            return null;
        }).when(repo).save(any(Order.class));

        Order result = service.createOrder(userId);

        assertThat(result.items()).hasSize(1);
        assertThat(result.totalPrice()).isEqualTo("19.99");
        assertThat(result.status()).isEqualTo(OrderStatus.PENDING_PAYMENT);

        verify(repo, times(1)).save(any(Order.class));
        assertThat(savedOrder[0]).isNotNull();
    }

    @Test
    @DisplayName("getOrder → returns order when found")
    void getOrder_success() {
        Order order = new Order(new OrderId(UUID.randomUUID()), List.of(new OrderItem(GameId.from(GAME1), BigDecimal.TEN)), OrderStatus.PENDING_PAYMENT, userId);

        when(repo.findById(order.id().value())).thenReturn(Optional.of(order));

        Order returned = service.getOrder(order.id());

        assertThat(returned).isSameAs(order);
    }

    @Test
    @DisplayName("getOrder → throws when not found")
    void getOrder_notFound() {
        UUID id = UUID.randomUUID();

        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOrder(new OrderId(id))).isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("createPaymentForOrder → throws OrderNotFoundException when order does not exist")
    void createPaymentForOrder_notFound() {
        UUID orderId = UUID.randomUUID();
        when(repo.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createPaymentForOrder(new OrderId(orderId), userId)).isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("createPaymentForOrder → throws OrderNotOwned when user mismatch")
    void createPaymentForOrder_wrongUser() {
        UUID orderId = UUID.randomUUID();

        Order order = new Order(new OrderId(orderId), List.of(new OrderItem(GameId.from(GAME1), BigDecimal.TEN)), OrderStatus.PENDING_PAYMENT, new UserId(UUID.randomUUID()));

        when(repo.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.createPaymentForOrder(new OrderId(orderId), userId)).isInstanceOf(OrderNotOwnedException.class).hasMessageContaining("User does not own this order");
    }

    @Test
    @DisplayName("createPaymentForOrder → creates payment, attaches paymentId, saves order, returns response")
    void createPaymentForOrder_success() {
        UUID orderId = UUID.randomUUID();

        Order order = new Order(new OrderId(orderId), List.of(new OrderItem(GameId.from(GAME1), BigDecimal.TEN)), OrderStatus.PENDING_PAYMENT, userId);

        when(repo.findById(orderId)).thenReturn(Optional.of(order));

        Payment payment = new Payment("PAY123", "https://checkout");
        when(paymentProvider.createPayment(order)).thenReturn(payment);

        final Order[] savedOrder = new Order[1];
        doAnswer(inv -> {
            savedOrder[0] = inv.getArgument(0);
            return null;
        }).when(repo).save(any(Order.class));

        String response = service.createPaymentForOrder(new OrderId(orderId), userId);

        assertThat(response).isEqualTo("https://checkout");

        verify(paymentProvider, times(1)).createPayment(order);
        verify(repo, times(1)).save(any(Order.class));

        assertThat(savedOrder[0].paymentId()).isEqualTo("PAY123");
    }
}