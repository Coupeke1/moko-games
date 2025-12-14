package be.kdg.team22.communicationservice.api.notification.converters;

import be.kdg.team22.communicationservice.domain.notification.NotificationType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NotificationTypeConverter implements Converter<String, NotificationType> {
    @Override
    public NotificationType convert(String source) {
        if (source == null) return null;

        String normalized = source.trim()
                .replace('-', '_')
                .toUpperCase();

        return NotificationType.valueOf(normalized);
    }
}