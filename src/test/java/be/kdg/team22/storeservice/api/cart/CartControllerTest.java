package be.kdg.team22.storeservice.api.cart;

import be.kdg.team22.storeservice.application.cart.CartService;
import be.kdg.team22.storeservice.config.TestSecurityConfig;
import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import be.kdg.team22.storeservice.domain.cart.CartItem;
import be.kdg.team22.storeservice.domain.cart.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import(TestSecurityConfig.class)
class CartControllerTest {

    private static final UUID USER_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID GAME_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    CartService cartService;
    @MockitoBean
    JwtDecoder jwtDecoder;

    private Cart sampleCart() {
        return new Cart(
                CartId.from(USER_ID),
                USER_ID,
                Set.of(new CartItem(GAME_ID))
        );
    }

    private String jwtUserId() {
        return USER_ID.toString();
    }

    @Test
    @DisplayName("GET /api/cart → returns existing cart")
    void getCart_returnsCart() throws Exception {
        Cart cart = sampleCart();

        when(cartService.getOrCreate(UserId.from(USER_ID))).thenReturn(cart);

        mockMvc.perform(get("/api/cart")
                        .with(jwt().jwt(builder -> builder.subject(jwtUserId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].gameId").value(GAME_ID.toString()));

        verify(cartService).getOrCreate(UserId.from(USER_ID));
    }

    @Test
    @DisplayName("GET /api/cart → missing JWT returns 401")
    void getCart_missingJwt_401() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/cart/items → successfully adds item")
    void addItem_succeeds() throws Exception {
        String body = """
                {
                  "gameId": "11111111-1111-1111-1111-111111111111"
                }
                """;

        mockMvc.perform(post("/api/cart/items")
                        .with(jwt().jwt(builder -> builder.subject(jwtUserId())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(cartService).addItem(UserId.from(USER_ID), GAME_ID);
    }

    @Test
    @DisplayName("POST /api/cart/items → invalid request returns 400")
    void addItem_invalidBody_400() throws Exception {
        String body = """
                {
                  "gameId": null
                }
                """;

        mockMvc.perform(post("/api/cart/items")
                        .with(jwt().jwt(builder -> builder.subject(jwtUserId())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/cart/items/{gameId} → removes item")
    void deleteItem_succeeds() throws Exception {
        mockMvc.perform(delete("/api/cart/items/{gameId}", GAME_ID)
                        .with(jwt().jwt(builder -> builder.subject(jwtUserId())))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(cartService).removeItem(UserId.from(USER_ID), GAME_ID);
    }

    @Test
    @DisplayName("DELETE /api/cart/items/{gameId} → missing JWT returns 401")
    void deleteItem_missingJwt_401() throws Exception {
        mockMvc.perform(delete("/api/cart/items/{gameId}", GAME_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/cart → clears cart")
    void clearCart_succeeds() throws Exception {
        mockMvc.perform(delete("/api/cart")
                        .with(jwt().jwt(builder -> builder.subject(jwtUserId())))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(cartService).clearCart(UserId.from(USER_ID));
    }
}