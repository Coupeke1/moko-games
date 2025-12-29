package be.kdg.team22.checkersservice.application.document;

import be.kdg.team22.checkersservice.domain.document.exceptions.ResourceNotFoundException;
import be.kdg.team22.checkersservice.infrastructure.document.ExternalDocumentRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class BotDocumentStartup {
    private final ExternalDocumentRepository documentRepository;

    public BotDocumentStartup(ExternalDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void uploadPlatformPdfOnStartup() throws Exception {
        ClassPathResource res = new ClassPathResource("pdf/Checkers-Team-22.pdf");

        if (!res.exists()) {
            throw ResourceNotFoundException.notFoundException();
        }

        byte[] pdfBytes = res.getInputStream().readAllBytes();
        String filename = res.getFilename() != null ? res.getFilename() : "platform.pdf";

        boolean ok = false;
        for (int i = 0; i < 6 && !ok; i++) {
            ok = documentRepository.uploadPdf(pdfBytes, filename);
            if (!ok) Thread.sleep(1000);
        }
    }
}