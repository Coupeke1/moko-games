package be.kdg.team22.communicationservice.api.notification.converters;

import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NotificationTypeConverter implements Converter<String, NotificationOrigin> {
    @Override
    public NotificationOrigin convert(String source) {
        if (source == null) return null;

        String normalized = source.trim()
                .replace('-', '_')
                .toUpperCase();

        return NotificationOrigin.valueOf(normalized);
    }
}