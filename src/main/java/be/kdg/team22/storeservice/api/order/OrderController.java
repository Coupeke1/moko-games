package be.kdg.team22.storeservice.api.order;

import be.kdg.team22.storeservice.api.order.models.OrderResponseModel;
import be.kdg.team22.storeservice.application.order.OrderService;
import be.kdg.team22.storeservice.application.order.PaymentService;
import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.order.Order;
import be.kdg.team22.storeservice.domain.order.OrderId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    private final PaymentService paymentService;

    public OrderController(final OrderService service, final PaymentService paymentService) {
        this.service = service;
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@AuthenticationPrincipal final Jwt token) {
        final UserId userId = UserId.get(token);

        final Order order = service.createOrder(userId);
        final String checkout = service.createPaymentForOrder(order.id(), userId);

        return ResponseEntity.ok(checkout);
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<OrderResponseModel> verifyPayment(@PathVariable final UUID id) {
        Order order = paymentService.verifyPayment(new OrderId(id));
        return ResponseEntity.ok(OrderResponseModel.from(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseModel> getOrder(@PathVariable final UUID id) {
        final Order order = service.getOrder(new OrderId(id));
        return ResponseEntity.ok(OrderResponseModel.from(order));
    }
}