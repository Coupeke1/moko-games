package be.kdg.team22.storeservice.api.order;

import be.kdg.team22.storeservice.application.order.OrderService;
import be.kdg.team22.storeservice.config.TestSecurityConfig;
import be.kdg.team22.storeservice.domain.order.Order;
import be.kdg.team22.storeservice.domain.order.OrderId;
import be.kdg.team22.storeservice.domain.order.OrderItem;
import be.kdg.team22.storeservice.domain.order.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(TestSecurityConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService service;

    private JwtAuthenticationToken jwtAuth() {
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", UUID.randomUUID().toString())
        );
        return new JwtAuthenticationToken(jwt);
    }

    @Test
    @DisplayName("POST /api/orders → creates order")
    void createOrder_success() throws Exception {
        UUID orderId = UUID.randomUUID();

        Order order = new Order(
                new OrderId(orderId),
                List.of(new OrderItem(UUID.randomUUID(), BigDecimal.valueOf(9.99))),
                OrderStatus.PENDING_PAYMENT
        );

        when(service.createOrder(any())).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                        .with(authentication(jwtAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
    }

    @Test
    void createOrder_error() throws Exception {
        when(service.createOrder(any())).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/api/orders")
                        .with(authentication(jwtAuth())))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getOrder_success() throws Exception {
        UUID id = UUID.randomUUID();

        Order order = new Order(
                new OrderId(id),
                List.of(new OrderItem(UUID.randomUUID(), BigDecimal.valueOf(5)),
                        new OrderItem(UUID.randomUUID(), BigDecimal.valueOf(10))),
                OrderStatus.PENDING_PAYMENT
        );

        when(service.getOrder(new OrderId(id))).thenReturn(order);

        mockMvc.perform(get("/api/orders/" + id)
                        .with(authentication(jwtAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.totalPrice").value(15))
                .andExpect(jsonPath("$.items.length()").value(2));
    }

    @Test
    void getOrder_notFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(service.getOrder(new OrderId(id)))
                .thenThrow(new RuntimeException("Order not found"));

        mockMvc.perform(get("/api/orders/" + id)
                        .with(authentication(jwtAuth())))
                .andExpect(status().is5xxServerError());
    }
}
