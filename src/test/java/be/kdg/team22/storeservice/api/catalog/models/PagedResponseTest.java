package be.kdg.team22.storeservice.api.catalog.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class PagedResponseTest {

    @Test
    void shouldHoldPagingValues() {
        List<String> items = List.of("a", "b");
        PagedResponse<String> pr = new PagedResponse<>(items, 1, 10, true);

        assertThat(pr.items()).containsExactly("a", "b");
        assertThat(pr.page()).isEqualTo(1);
        assertThat(pr.size()).isEqualTo(10);
        assertThat(pr.last()).isTrue();
    }
}