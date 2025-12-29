package be.kdg.team22.tictactoeservice.infrastructure.document;

import be.kdg.team22.tictactoeservice.domain.document.exceptions.BotServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Repository
public class ExternalDocumentRepository {

    private final RestClient client;

    public ExternalDocumentRepository(
            @Qualifier("documentService") final RestClient client
    ) {
        this.client = client;
    }

    public boolean uploadPdf(byte[] pdfBytes, String filename) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", new ByteArrayResource(pdfBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        });

        try {
            ResponseEntity<Void> response = client.post()
                    .uri("/upload-document")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            throw BotServiceException.requestFailed(ex);
        }
    }
}