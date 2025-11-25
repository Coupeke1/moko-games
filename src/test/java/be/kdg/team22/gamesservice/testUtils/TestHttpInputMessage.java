package be.kdg.team22.gamesservice.testUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TestHttpInputMessage implements HttpInputMessage {

    @Override
    public InputStream getBody() {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public HttpHeaders getHeaders() {
        return new HttpHeaders();
    }
}
