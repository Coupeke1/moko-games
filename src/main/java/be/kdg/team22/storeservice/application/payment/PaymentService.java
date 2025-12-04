package be.kdg.team22.storeservice.application.payment;

import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.domain.order.Order;
import be.kdg.team22.storeservice.domain.order.OrderRepository;
import be.kdg.team22.storeservice.domain.order.OrderStatus;
import be.kdg.team22.storeservice.infrastructure.payment.ExternalPaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final ExternalPaymentRepository paymentRepository;
    private final OrderRepository orderRepo;
    private final CartService cartService;

    public PaymentService(ExternalPaymentRepository paymentRepository, OrderRepository orderRepo, CartService cartService) {
        this.paymentRepository = paymentRepository;
        this.orderRepo = orderRepo;
        this.cartService = cartService;
    }

    public void process(String paymentId) {
        var payment = paymentRepository.getPayment(paymentId);

        Order order = orderRepo.findByPaymentId(paymentId)
                .orElseThrow();

        switch (payment.status()) {
            case "paid" -> handlePaid(order);
            case "canceled" -> order.updateStatus(OrderStatus.CANCELED);
            case "expired" -> order.updateStatus(OrderStatus.EXPIRED);
            case "failed" -> order.updateStatus(OrderStatus.FAILED);
        }

        orderRepo.save(order);
    }

    private void handlePaid(Order order) {
        order.updateStatus(OrderStatus.PAID);
        cartService.clearCart(order.userId());
    }
}
