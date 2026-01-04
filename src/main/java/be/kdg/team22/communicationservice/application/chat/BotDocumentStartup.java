package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.bot.BotChatRepository;
import be.kdg.team22.communicationservice.domain.chat.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BotDocumentStartup {
    private static final Logger logger = LoggerFactory.getLogger(BotDocumentStartup.class);

    private static final int STARTUP_MAX_RETRIES = 5;
    private static final int START_RETRY_DELAY_MS = 2000;
    private static final int SCHEDULED_RETRY_INTERVAL_MS = 60000;
    private static final String PDF_RESOURCE_PATH = "pdf/Platform-Team-22.pdf";

    private final BotChatRepository botRepo;
    private volatile boolean pdfLoaded = false;
    private volatile byte[] pdfBytes;
    private volatile String filename;

    public BotDocumentStartup(BotChatRepository botRepo) {
        this.botRepo = botRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void uploadPlatformPdfOnStartup() throws Exception {
        logger.info("Starting uploading platform pdf on startup to bot service...");

        try {
            loadPdfFile();

            if(uploadPdfWithRetries(STARTUP_MAX_RETRIES, START_RETRY_DELAY_MS)) {
                pdfLoaded = true;
                logger.info("Platform PDF '{}' successfully uploaded on startup", filename);
            } else {
                logger.warn("Failed to upload platform PDF on startup. Background retries will continue every {} seconds",
                        SCHEDULED_RETRY_INTERVAL_MS / 1000);
            }
        } catch (Exception e) {
            logger.error("Error during PDF startup initialization: {}", e.getMessage(), e);
        }
    }

    private void loadPdfFile() throws Exception {
        ClassPathResource res = new ClassPathResource(PDF_RESOURCE_PATH);

        if (!res.exists()) {
            logger.error("Platform pdf file does not exist at classpath:{}", PDF_RESOURCE_PATH);
            throw ResourceNotFoundException.notFoundException();
        }

        this.pdfBytes = res.getInputStream().readAllBytes();
        this.filename = res.getFilename() != null ? res.getFilename() : "Platform-Team-22.pdf";
        logger.debug("PDF file loaded: {} (size: {} bytes)", filename, pdfBytes.length);
    }

    @Scheduled(fixedDelay = SCHEDULED_RETRY_INTERVAL_MS)
    public void retryUploadPdfInBackground() {
        if (pdfLoaded) return;

        if (pdfBytes == null) {
            try {
                loadPdfFile();
            } catch (Exception e) {
                logger.warn("Failed to load PDF file in background retry: {}", e.getMessage());
                return;
            }
        }

        logger.debug("Attempting background PDF upload...");
        if (uploadPdfWithRetries(STARTUP_MAX_RETRIES, START_RETRY_DELAY_MS)) {
            pdfLoaded = true;
            logger.info("Platform PDF '{}' successfully uploaded in background retry", filename);
        } else {
            logger.warn("Background PDF upload attempt failed. Will retry in {} seconds",
                    SCHEDULED_RETRY_INTERVAL_MS / 1000);
        }
    }

    private boolean uploadPdfWithRetries(final int maxRetries, final int delayMs) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                if (botRepo.uploadPdf(pdfBytes, filename)) return true;
                logger.warn("PDF upload attempt {}/{} failed", attempt, maxRetries);
            } catch (Exception e) {
                logger.warn("PDF upload attempt {}/{} encountered error: {}", attempt, maxRetries, e.getMessage());
            }

            if (attempt < maxRetries) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Upload retry interrupted", e);
                    return false;
                }
            }
        }
        return false;
    }
}