package be.kdg.team22.storeservice.application.order;

import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogRepository;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.domain.order.*;
import be.kdg.team22.storeservice.domain.order.exceptions.OrderEmptyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository repo;
    private final CartService cartService;
    private final GameCatalogRepository catalog;

    public OrderService(final OrderRepository repo,
                        final CartService cartService,
                        final GameCatalogRepository catalog) {
        this.repo = repo;
        this.cartService = cartService;
        this.catalog = catalog;
    }

    public Order createOrder(final UserId userId) {

        final Cart cart = cartService.get(userId);

        if (cart.isEmpty())
            throw new OrderEmptyException();

        List<OrderItem> items = cart.items().stream()
                .map(i -> {
                    GameCatalogEntry entry = catalog.findById(i.gameId())
                            .orElseThrow(() -> new GameNotFoundException(i.gameId()));
                    return new OrderItem(i.gameId(), entry.getPrice());
                })
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
                .orElseThrow();
    }
}