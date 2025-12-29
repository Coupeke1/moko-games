package be.kdg.team22.tictactoeservice.infrastructure.communication;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class ExternalBotCommunicationRepository {
    private final RestClient client;

    public ExternalBotCommunicationRepository(@Qualifier("botRepository") RestClient client) {
        this.client = client;
    }

    public ResponseEntity<String> uploadDocument(byte[] bytes, String filename) {
        ByteArrayResource fileResource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        HttpHeaders partHeaders = new HttpHeaders();
        partHeaders.setContentType(MediaType.APPLICATION_PDF);
        partHeaders.setContentDisposition(
                ContentDisposition.formData().name("file").filename(filename).build()
        );

        HttpEntity<ByteArrayResource> filePart = new HttpEntity<>(fileResource, partHeaders);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", filePart);

        return client.post()
                .uri("/upload-document")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toEntity(String.class);
    }
}