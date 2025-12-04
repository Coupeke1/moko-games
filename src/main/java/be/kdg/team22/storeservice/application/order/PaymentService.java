package be.kdg.team22.storeservice.application.order;

import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.application.catalog.services.StoreService;
import be.kdg.team22.storeservice.domain.order.Order;
import be.kdg.team22.storeservice.domain.order.OrderId;
import be.kdg.team22.storeservice.domain.order.OrderRepository;
import be.kdg.team22.storeservice.domain.order.OrderStatus;
import be.kdg.team22.storeservice.domain.order.exceptions.OrderNotFoundException;
import be.kdg.team22.storeservice.domain.order.exceptions.PaymentIncompleteException;
import be.kdg.team22.storeservice.domain.payment.PaymentProvider;
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

    public PaymentService(
            PaymentProvider paymentProvider,
            OrderRepository orderRepo,
            CartService cartService,
            StoreService storeService) {
        this.paymentProvider = paymentProvider;
        this.orderRepo = orderRepo;
        this.cartService = cartService;
        this.storeService = storeService;
    }

    public void processWebhook(final String paymentId) {

        Order order = orderRepo.findByPaymentId(paymentId)
                .orElseThrow(() -> new OrderNotFoundException(paymentId));

        PaymentStatus status = paymentProvider.getPaymentStatus(paymentId);
        String s = status.value();

        switch (s) {
            case "paid" -> {
                order.updateStatus(OrderStatus.PAID);
                cartService.clearCart(order.userId());
                order.items().forEach(item -> storeService.recordPurchase(item.gameId()));
            }
            case "canceled" -> order.updateStatus(OrderStatus.CANCELED);
            case "expired" -> order.updateStatus(OrderStatus.EXPIRED);
            case "failed" -> order.updateStatus(OrderStatus.FAILED);
        }
        orderRepo.save(order);
    }

    public Order verifyPayment(final OrderId orderId) {
        Order order = orderRepo.findById(orderId.value())
                .orElseThrow(OrderNotFoundException::new);

        String paymentId = order.paymentId();
        PaymentStatus status = paymentProvider.getPaymentStatus(paymentId);

        if (status == PaymentStatus.PAID) {
            order.updateStatus(OrderStatus.PAID);
            cartService.clearCart(order.userId());
            order.items().forEach(item -> storeService.recordPurchase(item.gameId()));
            orderRepo.save(order);
            return order;
        }

        throw new PaymentIncompleteException();
    }
}