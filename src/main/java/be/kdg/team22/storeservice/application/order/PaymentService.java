package be.kdg.team22.storeservice.application.order;

import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.domain.order.Order;
import be.kdg.team22.storeservice.domain.order.OrderRepository;
import be.kdg.team22.storeservice.domain.order.OrderStatus;
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

    public PaymentService(
            PaymentProvider paymentProvider,
            OrderRepository orderRepo,
            CartService cartService) {
        this.paymentProvider = paymentProvider;
        this.orderRepo = orderRepo;
        this.cartService = cartService;
    }

    public void processWebhook(String paymentId) {

        Order order = orderRepo.findByPaymentId(paymentId)
                .orElseThrow(() -> new IllegalStateException("Order not found for payment"));

        PaymentStatus status = paymentProvider.getPaymentStatus(paymentId);
        String s = status.toString().toLowerCase();

        switch (s) {
            case "paid" -> {
                order.updateStatus(OrderStatus.PAID);
                cartService.clearCart(order.userId());
            }
            case "canceled" -> order.updateStatus(OrderStatus.CANCELED);
            case "expired" -> order.updateStatus(OrderStatus.EXPIRED);
            case "failed" -> order.updateStatus(OrderStatus.FAILED);
        }
        orderRepo.save(order);
    }
}