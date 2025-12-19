package be.kdg.team22.gameaclservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "chess-info")
public class ChessInfoProperties {
    private String backendUrl;
    private BigDecimal price;
    private String category;

    public String backendUrl() {
        return backendUrl;
    }

    public void setBackendUrl(String backendUrl) {
        this.backendUrl = backendUrl;
    }

    public BigDecimal price() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String category() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
