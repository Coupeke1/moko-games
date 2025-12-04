package be.kdg.team22.storeservice.infrastructure.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mollie")
public class MollieProperties {
    private String redirectUrl;
    private String webhookUrl;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
}