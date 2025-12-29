package be.kdg.team22.tictactoeservice.infrastructure.document;

import be.kdg.team22.tictactoeservice.domain.document.exceptions.BotServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Repository
public class ExternalDocumentRepository {

    private final Logger logger = LoggerFactory.getLogger(ExternalDocumentRepository.class);
    private final RestClient primaryClient;
    private final RestClient fallbackClient;

    public ExternalDocumentRepository(
            @Qualifier("documentService") final RestClient primaryClient,
            @Qualifier("documentServiceFallback") final RestClient fallbackClient
    ) {
        this.primaryClient = primaryClient;
        this.fallbackClient = fallbackClient;
    }

    public boolean uploadPdf(byte[] pdfBytes, String filename) {
        try {
            return tryUploadWithClient(primaryClient, pdfBytes, filename);
        } catch (ResourceAccessException ex) {
            // "Niet bereikbaar" => fallback proberen
            logger.warn("Primary document service not reachable, trying fallback. {}", ex.getMessage());
            try {
                return tryUploadWithClient(fallbackClient, pdfBytes, filename);
            } catch (RestClientException fallbackEx) {
                throw BotServiceException.requestFailed(fallbackEx);
            }
        } catch (RestClientException ex) {
            // Wel bereikbaar maar request faalt (4xx/5xx/etc) => geen fallback
            throw BotServiceException.requestFailed(ex);
        }
    }

    private boolean tryUploadWithClient(RestClient client, byte[] pdfBytes, String filename) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(pdfBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        });

        ResponseEntity<Void> response = client.post()
                .uri("/upload-document")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .toBodilessEntity();

        return response.getStatusCode().is2xxSuccessful();
    }
}