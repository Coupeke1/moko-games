package be.kdg.team22.storeservice.application.cart;

import be.kdg.team22.storeservice.domain.cart.*;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartEmptyException;
import be.kdg.team22.storeservice.domain.cart.exceptions.CartNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceSociableTest {

    private final UUID USER_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private final UUID GAME_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    @Mock
    CartRepository repo;
    CartService service;

    @BeforeEach
    void setup() {
        service = new CartService(repo);
    }

    private Cart sampleCart() {
        return new Cart(
                CartId.create(),
                USER_ID,
                Set.of(new CartItem(GAME_ID))
        );
    }

    private UserId uid() {
        return UserId.from(USER_ID);
    }

    @Test
    void getOrCreate_returnsExistingCart() {
        Cart existing = sampleCart();
        when(repo.findByUserId(USER_ID)).thenReturn(Optional.of(existing));

        Cart result = service.getOrCreate(uid());

        assertThat(result).isSameAs(existing);
        verify(repo, never()).save(any());
    }

    @Test
    void getOrCreate_createsNewCartWhenMissing() {
        when(repo.findByUserId(USER_ID)).thenReturn(Optional.empty());

        Cart result = service.getOrCreate(uid());

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(USER_ID);
        verify(repo).save(any(Cart.class));
    }

    @Test
    void get_returnsCartWhenExists() {
        Cart cart = sampleCart();
        when(repo.findByUserId(USER_ID)).thenReturn(Optional.of(cart));

        Cart result = service.get(uid());

        assertThat(result).isSameAs(cart);
    }

    @Test
    void get_whenMissing_throwsCartNotFound() {
        when(repo.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(uid()))
                .isInstanceOf(CartNotFoundException.class)
                .hasMessageContaining(USER_ID.toString());
    }

    @Test
    void addItem_addsNewItemAndSaves() {
        Cart cart = new Cart(CartId.create(), USER_ID);
        when(repo.findByUserId(USER_ID)).thenReturn(Optional.of(cart));

        service.addItem(uid(), GAME_ID);

        assertThat(cart.items())
                .extracting(CartItem::gameId)
                .contains(GAME_ID);

        verify(repo).save(cart);
    }

    @Test
    void addItem_createsCartIfMissing() {
        when(repo.findByUserId(USER_ID)).thenReturn(Optional.empty());

        service.addItem(uid(), GAME_ID);

        verify(repo, times(2)).save(any(Cart.class));
    }

    @Test
    void removeItem_removesItemAndSaves() {
        Cart cart = sampleCart();
        when(repo.findByUserId(USER_ID)).thenReturn(Optional.of(cart));

        service.removeItem(uid(), GAME_ID);

        assertThat(cart.items()).isEmpty();
        verify(repo).save(cart);
    }

    @Test
    void removeItem_cartMissing_throwsNotFound() {
        when(repo.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeItem(uid(), GAME_ID))
                .isInstanceOf(CartNotFoundException.class);
    }

    @Test
    void clearCart_clearsAndSaves() {
        Cart cart = sampleCart();
        when(repo.findByUserId(USER_ID)).thenReturn(Optional.of(cart));

        service.clearCart(uid());

        assertThat(cart.items()).isEmpty();
        verify(repo).save(cart);
    }

    @Test
    void clearCart_whenEmpty_throwsCartEmptyException() {
        Cart empty = new Cart(CartId.create(), USER_ID);
        when(repo.findByUserId(USER_ID)).thenReturn(Optional.of(empty));

        assertThatThrownBy(() -> service.clearCart(uid()))
                .isInstanceOf(CartEmptyException.class)
                .hasMessageContaining(USER_ID.toString());
    }

    @Test
    void clearCart_missingCart_throwsNotFound() {
        when(repo.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.clearCart(uid()))
                .isInstanceOf(CartNotFoundException.class);
    }
}