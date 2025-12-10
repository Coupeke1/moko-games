package be.kdg.team22.checkersservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "game-info")
public class GameInfoProperties {
    private String name;
    private String title;
    private String description;
    private String image;
    private String backendUrl;
    private String frontendUrl;
    private String startEndpoint;
    private String healthEndpoint;
    private BigDecimal price;
    private String category;

    public String name() {
        return name;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String image() {
        return image;
    }

    public String backendUrl() {
        return backendUrl;
    }

    public String frontendUrl() {
        return frontendUrl;
    }

    public String startEndpoint() {
        return startEndpoint;
    }

    public String healthEndpoint() {
        return healthEndpoint;
    }

    public BigDecimal price() {
        return price;
    }

    public String category() {
        return category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setBackendUrl(String backendUrl) {
        this.backendUrl = backendUrl;
    }

    public void setFrontendUrl(String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    public void setStartEndpoint(String startEndpoint) {
        this.startEndpoint = startEndpoint;
    }

    public void setHealthEndpoint(String healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
