package be.kdg.team22.storeservice.application.order;

import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.order.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository repo;
    private final CartService cartService;

    public OrderService(final OrderRepository repo,
                        final CartService cartService) {
        this.repo = repo;
        this.cartService = cartService;
    }

    public Order createOrder(final UserId userId) {

        final Cart cart = cartService.get(userId);

        if (cart.items().isEmpty())
            throw new RuntimeException("Cannot create order from empty cart");

        List<OrderItem> items = cart.items().stream()
                .map(i -> new OrderItem(i.gameId(), i.price()))
                .toList();

        Order order = new Order(
                OrderId.create(),
                items,
                OrderStatus.PENDING_PAYMENT
        );

        repo.save(order);

        return order;
    }

    public Order getOrder(final OrderId id) {
        return repo.findById(id.value())
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}