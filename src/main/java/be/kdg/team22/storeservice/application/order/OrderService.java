package be.kdg.team22.storeservice.application.order;

import be.kdg.team22.storeservice.api.order.models.PaymentResponse;
import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogRepository;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.domain.order.*;
import be.kdg.team22.storeservice.domain.order.exceptions.OrderEmptyException;
import be.kdg.team22.storeservice.domain.order.exceptions.OrderNotFoundException;
import be.kdg.team22.storeservice.domain.order.exceptions.OrderNotOwnedException;
import be.kdg.team22.storeservice.domain.payment.Payment;
import be.kdg.team22.storeservice.domain.payment.PaymentProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {
    private final OrderRepository repository;
    private final CartService cartService;
    private final GameCatalogRepository catalog;
    private final PaymentProvider paymentProvider;

    public OrderService(OrderRepository repository,
                        CartService cartService,
                        GameCatalogRepository catalog,
                        PaymentProvider paymentProvider) {
        this.repository = repository;
        this.cartService = cartService;
        this.catalog = catalog;
        this.paymentProvider = paymentProvider;
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
                OrderStatus.PENDING_PAYMENT,
                userId
        );

        repository.save(order);

        return order;
    }

    public Order getOrder(final OrderId id) {
        return repository.findById(id.value())
                .orElseThrow(OrderNotFoundException::new);
    }

    public PaymentResponse createPaymentForOrder(OrderId id, UserId userId) {
        Order order = repository.findById(id.value())
                .orElseThrow(OrderNotFoundException::new);

        if (!order.userId().equals(userId)) throw new OrderNotOwnedException("User does not own this order");

        Payment payment = paymentProvider.createPayment(order);

        order.attachPaymentId(payment.id());
        repository.save(order);

        return new PaymentResponse(payment.checkoutUrl());
    }
}