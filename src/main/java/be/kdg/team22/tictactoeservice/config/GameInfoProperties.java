package be.kdg.team22.tictactoeservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

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
    private List<Achievement> achievements;

    private SettingsDefinition settingsDefinition;

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

    public List<Achievement> achievements() {
        return achievements;
    }

    public SettingsDefinition settingsDefinition() {
        return settingsDefinition;
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

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public void setSettingsDefinition(SettingsDefinition settingsDefinition) {
        this.settingsDefinition = settingsDefinition;
    }

    public static class Achievement {
        private String key;
        private String name;
        private String description;
        private int levels;

        public String key() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String name() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String description() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int levels() {
            return levels;
        }

        public void setLevels(int levels) {
            this.levels = levels;
        }
    }

    public static class SettingsDefinition {
        private List<Setting> settings;

        public List<Setting> settings() {
            return settings;
        }

        public void setSettings(List<Setting> settings) {
            this.settings = settings;
        }
    }

    public static class Setting {
        private String name;
        private String type;
        private boolean required;
        private Integer min;
        private Integer max;
        private List<String> allowedValues;
        private Object defaultValue;

        public String name() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String type() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean required() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public Integer min() {
            return min;
        }

        public void setMin(Integer min) {
            this.min = min;
        }

        public Integer max() {
            return max;
        }

        public void setMax(Integer max) {
            this.max = max;
        }

        public List<String> allowedValues() {
            return allowedValues;
        }

        public void setAllowedValues(List<String> allowedValues) {
            this.allowedValues = allowedValues;
        }

        public Object defaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}
