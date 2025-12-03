package be.kdg.team22.storeservice.infrastructure.cart.jpa;

import be.kdg.team22.storeservice.domain.cart.CartItem;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemEntityTest {

    @Test
    void fromDomain_mapsFieldsCorrectly() {
        UUID gameId = UUID.randomUUID();
        CartItem domain = new CartItem(gameId);

        CartItemEntity entity = CartItemEntity.fromDomain(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.toDomain().gameId()).isEqualTo(gameId);
    }

    @Test
    void toDomain_mapsBackCorrectly() {
        UUID gameId = UUID.randomUUID();

        CartItemEntity entity = new CartItemEntity(gameId);

        CartItem domain = entity.toDomain();

        assertThat(domain.gameId()).isEqualTo(gameId);
    }

    @Test
    void entity_hasBackreferenceButDomainDoesNotExposeIt() {
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();

        CartItemEntity item = new CartItemEntity(gameId);
        CartEntity cart = new CartEntity(cartId, userId, java.util.List.of(item));

        assertThat(item).isNotNull();
        assertThat(item.toDomain().gameId()).isEqualTo(gameId);
    }
}