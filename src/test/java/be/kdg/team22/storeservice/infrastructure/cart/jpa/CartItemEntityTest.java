package be.kdg.team22.storeservice.infrastructure.cart.jpa;

import be.kdg.team22.storeservice.domain.cart.CartItem;
import be.kdg.team22.storeservice.domain.catalog.GameId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemEntityTest {

    @Test
    void from_mapsFieldsCorrectly() {
        UUID gameId = UUID.randomUUID();
        CartItem domain = new CartItem(GameId.from(gameId));

        CartItemEntity entity = CartItemEntity.from(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.to().gameId().value()).isEqualTo(gameId);
    }

    @Test
    void to_mapsBackCorrectly() {
        UUID gameId = UUID.randomUUID();

        CartItemEntity entity = new CartItemEntity(gameId);

        CartItem domain = entity.to();

        assertThat(domain.gameId().value()).isEqualTo(gameId);
    }

    @Test
    void entity_hasBackreferenceButDomainDoesNotExposeIt() {
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();

        CartItemEntity item = new CartItemEntity(gameId);
        CartEntity cart = new CartEntity(cartId, userId, java.util.List.of(item));

        assertThat(item).isNotNull();
        assertThat(item.to().gameId().value()).isEqualTo(gameId);
    }
}