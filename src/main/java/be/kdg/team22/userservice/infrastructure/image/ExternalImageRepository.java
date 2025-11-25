package be.kdg.team22.userservice.infrastructure.image;

import be.kdg.team22.userservice.infrastructure.image.exceptions.NotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalImageRepository {
    private final RestClient client;

    public ExternalImageRepository(@Qualifier("catService") final RestClient client) {
        this.client = client;
    }

    public ImageResponse get() {
        try {
            ImageResponse[] responses = client.get().uri("/images/search").retrieve().body(ImageResponse[].class);
            return responses != null && responses.length > 0 ? responses[0] : null;
        } catch (
                HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND)
                return null;

            throw exception;
        } catch (RestClientException exception) {
            System.out.println(exception.getMessage());
            throw new NotReachableException();
        }
    }
}