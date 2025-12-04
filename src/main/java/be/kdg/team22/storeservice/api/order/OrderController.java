package be.kdg.team22.storeservice.api.order;

import be.kdg.team22.storeservice.api.order.models.OrderResponseModel;
import be.kdg.team22.storeservice.api.order.models.PaymentResponse;
import be.kdg.team22.storeservice.application.order.OrderService;
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

    public OrderController(final OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponseModel> createOrder(
            @AuthenticationPrincipal final Jwt jwt
    ) {
        final UserId userId = UserId.get(jwt);
        final Order order = service.createOrder(userId);
        return ResponseEntity.ok(OrderResponseModel.from(order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseModel> getOrder(
            @PathVariable final UUID orderId
    ) {
        final Order order = service.getOrder(new OrderId(orderId));
        return ResponseEntity.ok(OrderResponseModel.from(order));
    }

    @PostMapping("/{orderId}/payment")
    public ResponseEntity<PaymentResponse> startPayment(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {

        PaymentResponse payment = service.createPaymentForOrder(
                new OrderId(orderId),
                UserId.get(jwt)
        );

        return ResponseEntity.ok(payment);
    }
}