package be.kdg.team22.storeservice.application.order;

import be.kdg.team22.storeservice.api.order.models.MolliePaymentResponse;
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
import be.kdg.team22.storeservice.infrastructure.payment.ExternalPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {
    private final OrderRepository repository;
    private final CartService cartService;
    private final GameCatalogRepository catalog;
    private final ExternalPaymentRepository paymentRepository;

    public OrderService(final OrderRepository repository,
                        final CartService cartService,
                        final GameCatalogRepository catalog,
                        final ExternalPaymentRepository paymentRepository) {
        this.repository = repository;
        this.cartService = cartService;
        this.catalog = catalog;
        this.paymentRepository = paymentRepository;
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

        repository.save(order);

        return order;
    }

    public Order getOrder(final OrderId id) {
        return repository.findById(id.value())
                .orElseThrow(OrderNotFoundException::new);
    }

    public PaymentResponse createPayment(final OrderId id, final UserId userId) {
        Order order = repository.findById(id.value())
                .orElseThrow(OrderNotFoundException::new);

        MolliePaymentResponse mollie = paymentRepository.createPayment(
                order.totalPrice(),
                buildRedirectUrl(id),
                buildWebhookUrl(),
                "Order " + id.value()
        );

        order.attachPaymentId(mollie.id());
        repository.save(order);

        return new PaymentResponse(mollie.checkoutUrl());
    }
}