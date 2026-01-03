package be.kdg.team22.tictactoeservice.application.document;

import be.kdg.team22.tictactoeservice.domain.document.exceptions.ResourceNotFoundException;
import be.kdg.team22.tictactoeservice.infrastructure.document.ExternalDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "bot-service", name = "upload-on-startup", havingValue = "true", matchIfMissing = true)
public class BotDocumentStartup {
    private static final Logger logger = LoggerFactory.getLogger(BotDocumentStartup.class);
    private final ExternalDocumentRepository documentRepository;

    public BotDocumentStartup(ExternalDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void uploadPlatformPdfOnStartup() throws Exception {
        ClassPathResource res = new ClassPathResource("pdf/Tic-Tac-Toe-Team-22.pdf");

        if (!res.exists()) {
            throw ResourceNotFoundException.notFoundException();
        }

        byte[] pdfBytes = res.getInputStream().readAllBytes();
        String filename = res.getFilename() != null ? res.getFilename() : "platform.pdf";

        final int ATTEMPTS = 6;
        for (int i = 0; i < ATTEMPTS; i++) {
            boolean ok = documentRepository.uploadPdf(pdfBytes, filename);
            if (!ok) {
                Thread.sleep(1000);
            } else {
                logger.info("Document '{}' successfully uploaded.", filename);
                return;
            }
        }
        logger.error("Failed to upload document '{}' after {} attempts.", filename, ATTEMPTS);
    }
}