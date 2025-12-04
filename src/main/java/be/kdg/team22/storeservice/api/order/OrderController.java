package be.kdg.team22.storeservice.api.order;

import be.kdg.team22.storeservice.api.order.models.OrderResponseModel;
import be.kdg.team22.storeservice.api.order.models.PaymentResponse;
import be.kdg.team22.storeservice.application.order.OrderService;
import be.kdg.team22.storeservice.application.payment.PaymentService;
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
    public ResponseEntity<PaymentResponse> createPayment(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {
        PaymentResponse response =
                service.createPayment(new OrderId(orderId), UserId.get(jwt));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mollie/webhook")
    public ResponseEntity<Void> webhook(@RequestParam("id") String paymentId) {
        paymentService.process(paymentId);
        return ResponseEntity.ok().build();
    }
}