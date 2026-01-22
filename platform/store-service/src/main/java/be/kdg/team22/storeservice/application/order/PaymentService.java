package be.kdg.team22.storeservice.application.order;

import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.application.catalog.StoreService;
import be.kdg.team22.storeservice.domain.order.Order;
import be.kdg.team22.storeservice.domain.order.OrderId;
import be.kdg.team22.storeservice.domain.order.OrderRepository;
import be.kdg.team22.storeservice.domain.order.OrderStatus;
import be.kdg.team22.storeservice.domain.order.events.GamesPurchasedEvent;
import be.kdg.team22.storeservice.domain.order.events.OrderCompletedEvent;
import be.kdg.team22.storeservice.domain.order.exceptions.OrderNotFoundException;
import be.kdg.team22.storeservice.domain.order.exceptions.PaymentIncompleteException;
import be.kdg.team22.storeservice.domain.payment.PaymentProvider;
import be.kdg.team22.storeservice.infrastructure.messaging.OrderEventPublisher;
import com.mollie.mollie.models.components.PaymentStatus;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class PaymentService {

    private final PaymentProvider paymentProvider;
    private final OrderRepository orderRepo;
    private final CartService cartService;
    private final StoreService storeService;
    private final OrderEventPublisher eventPublisher;

    public PaymentService(final PaymentProvider paymentProvider, final OrderRepository orderRepo, final CartService cartService, final StoreService storeService, final OrderEventPublisher eventPublisher) {
        this.paymentProvider = paymentProvider;
        this.orderRepo = orderRepo;
        this.cartService = cartService;
        this.storeService = storeService;
        this.eventPublisher = eventPublisher;
    }

    public void processWebhook(final String paymentId) {
        Order order = orderRepo.findByPaymentId(paymentId).orElseThrow(() -> new OrderNotFoundException(paymentId));

        PaymentStatus status = paymentProvider.getPaymentStatus(paymentId);
        String s = status.value();

        switch (s) {
            case "paid" -> {
                order.updateStatus(OrderStatus.PAID);
                cartService.clear(order.userId());
                order.items().forEach(item -> storeService.recordPurchase(item.gameId()));

                eventPublisher.publishOrderCompleted(new OrderCompletedEvent(order.userId().value(), order.id().value(), order.totalPrice()));

                eventPublisher.publishGamesPurchased(new GamesPurchasedEvent(order.userId().value(), order.items().stream().map(item -> item.gameId().value()).toList()));
            }
            case "canceled" ->
                    order.updateStatus(OrderStatus.CANCELED);
            case "expired" ->
                    order.updateStatus(OrderStatus.EXPIRED);
            case "failed" ->
                    order.updateStatus(OrderStatus.FAILED);
        }
        orderRepo.save(order);
    }

    public Order verifyPayment(final OrderId orderId) {
        Order order = orderRepo.findById(orderId.value()).orElseThrow(OrderNotFoundException::new);

        String paymentId = order.paymentId();
        PaymentStatus status = paymentProvider.getPaymentStatus(paymentId);

        if (status == PaymentStatus.PAID) {
            order.updateStatus(OrderStatus.PAID);
            cartService.clear(order.userId());
            order.items().forEach(item -> storeService.recordPurchase(item.gameId()));
            orderRepo.save(order);

            eventPublisher.publishOrderCompleted(new OrderCompletedEvent(order.userId().value(), order.id().value(), order.totalPrice()));

            eventPublisher.publishGamesPurchased(new GamesPurchasedEvent(order.userId().value(), order.items().stream().map(item -> item.gameId().value()).toList()));

            return order;
        }

        throw new PaymentIncompleteException();
    }
}