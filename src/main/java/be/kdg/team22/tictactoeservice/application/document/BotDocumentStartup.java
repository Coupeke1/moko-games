package be.kdg.team22.tictactoeservice.application.document;

import be.kdg.team22.tictactoeservice.domain.document.exceptions.ResourceNotFoundException;
import be.kdg.team22.tictactoeservice.infrastructure.document.ExternalDocumentRepository;
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
        ClassPathResource res = new ClassPathResource("pdf/Tic-Tac-Toe-Team-22.pdf");

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