package be.kdg.team22.communicationservice.api.notification.converters;

import be.kdg.team22.communicationservice.application.queries.NotificationFilter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NotificationReadFilterConverter implements Converter<String, NotificationFilter> {
    @Override
    public NotificationFilter convert(String source) {
        if (source == null) return null;
        return NotificationFilter.valueOf(source.trim().toUpperCase());
    }
}
