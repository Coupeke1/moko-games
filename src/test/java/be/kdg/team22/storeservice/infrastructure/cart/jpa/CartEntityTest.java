package be.kdg.team22.storeservice.infrastructure.cart.jpa;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import be.kdg.team22.storeservice.domain.cart.CartItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CartEntityTest {

    @Test
    void fromDomain_mapsAllFields_correctly() {
        UUID userId = UUID.randomUUID();
        CartId cartId = CartId.create();

        Cart domain = new Cart(
                cartId,
                userId,
                Set.of(new CartItem(UUID.fromString("11111111-1111-1111-1111-111111111111")))
        );

        CartEntity entity = CartEntity.fromDomain(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.userId()).isEqualTo(userId);
        assertThat(entity.id()).isEqualTo(cartId.value());
        assertThat(entity.items()).hasSize(1);

        CartItemEntity item = entity.items().getFirst();
        assertThat(item.toDomain().gameId())
                .isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
    }

    @Test
    void fromDomain_setsBackreferences_correctly() {
        UUID userId = UUID.randomUUID();
        CartId cartId = CartId.create();

        Cart domain = new Cart(cartId, userId, Set.of(new CartItem(UUID.randomUUID())));

        CartEntity entity = CartEntity.fromDomain(domain);

        assertThat(entity.items()).allMatch(i -> i.cart() == entity);
    }

    @Test
    void toDomain_mapsBackCorrectly() {
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();

        CartItemEntity item = new CartItemEntity(gameId);
        CartEntity entity = new CartEntity(cartId, userId, List.of(item));

        Cart domain = entity.toDomain();

        assertThat(domain.id().value()).isEqualTo(cartId);
        assertThat(domain.userId()).isEqualTo(userId);
        assertThat(domain.items()).hasSize(1);

        CartItem mapped = domain.items().iterator().next();
        assertThat(mapped.gameId()).isEqualTo(gameId);
    }

    @Test
    void toDomain_usesSetForItems() {
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UUID gameId = UUID.randomUUID();
        CartItemEntity item1 = new CartItemEntity(gameId);
        CartItemEntity item2 = new CartItemEntity(gameId);

        CartEntity entity = new CartEntity(cartId, userId, List.of(item1, item2));

        Cart domain = entity.toDomain();

        assertThat(domain.items()).hasSize(1);
    }
}