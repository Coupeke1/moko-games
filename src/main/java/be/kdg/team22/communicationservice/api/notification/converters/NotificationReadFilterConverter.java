package be.kdg.team22.communicationservice.api.notification.converters;

import be.kdg.team22.communicationservice.application.queries.NotificationReadFilter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NotificationReadFilterConverter implements Converter<String, NotificationReadFilter> {
    @Override
    public NotificationReadFilter convert(String source) {
        if (source == null) return null;
        return NotificationReadFilter.valueOf(source.trim().toUpperCase());
    }
}
