package be.kdg.team22.storeservice.application.order;

import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import be.kdg.team22.storeservice.domain.cart.CartItem;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogRepository;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.domain.order.*;
import be.kdg.team22.storeservice.domain.order.exceptions.OrderEmptyException;
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
    private GameCatalogRepository catalog;
    private OrderService service;

    @BeforeEach
    void setup() {
        repo = mock(OrderRepository.class);
        cartService = mock(CartService.class);
        catalog = mock(GameCatalogRepository.class);

        service = new OrderService(repo, cartService, catalog);
    }

    @Test
    @DisplayName("createOrder → throws OrderEmptyException when cart is empty")
    void createOrder_emptyCart() {
        Cart empty = new Cart(CartId.create(), USER);

        when(cartService.get(userId)).thenReturn(empty);

        assertThatThrownBy(() -> service.createOrder(userId))
                .isInstanceOf(OrderEmptyException.class);
    }

    @Test
    @DisplayName("createOrder → throws GameNotFoundException when catalog has no entry")
    void createOrder_gameNotFound() {
        Cart cart = new Cart(CartId.create(), USER, Set.of(new CartItem(GAME1)));

        when(cartService.get(userId)).thenReturn(cart);
        when(catalog.findById(GAME1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createOrder(userId))
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    @DisplayName("createOrder → creates valid order with catalog prices")
    void createOrder_success() {
        Cart cart = new Cart(CartId.create(), USER, Set.of(new CartItem(GAME1)));

        when(cartService.get(userId)).thenReturn(cart);

        GameCatalogEntry entry = new GameCatalogEntry(GAME1, BigDecimal.valueOf(19.99), null);
        when(catalog.findById(GAME1)).thenReturn(Optional.of(entry));

        doAnswer(inv -> {
            Order saved = inv.getArgument(0);
            assertThat(saved.items()).hasSize(1);
            assertThat(saved.items().getFirst().price()).isEqualTo("19.99");
            assertThat(saved.status()).isEqualTo(OrderStatus.PENDING_PAYMENT);
            return null;
        }).when(repo).save(any(Order.class));

        Order result = service.createOrder(userId);

        assertThat(result.items()).hasSize(1);
        assertThat(result.totalPrice()).isEqualTo("19.99");
    }

    @Test
    @DisplayName("getOrder → returns order when found")
    void getOrder_success() {
        Order order = new Order(
                new OrderId(UUID.randomUUID()),
                List.of(new OrderItem(GAME1, BigDecimal.TEN)),
                OrderStatus.PENDING_PAYMENT
        );

        when(repo.findById(order.id().value())).thenReturn(Optional.of(order));

        Order returned = service.getOrder(order.id());

        assertThat(returned).isSameAs(order);
    }

    @Test
    @DisplayName("getOrder → throws when not found")
    void getOrder_notFound() {
        UUID id = UUID.randomUUID();

        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOrder(new OrderId(id)))
                .isInstanceOf(NoSuchElementException.class);
    }
}