package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.bot.BotChatRepository;
import be.kdg.team22.communicationservice.domain.chat.exceptions.ResourceNotFoundException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class BotDocumentStartup {
    private final BotChatRepository botRepo;

    public BotDocumentStartup(BotChatRepository botRepo) {
        this.botRepo = botRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void uploadPlatformPdfOnStartup() throws Exception {
        ClassPathResource res = new ClassPathResource("pdf/platform-Team-22.pdf");

        if (!res.exists()) {
            throw ResourceNotFoundException.notFoundException();
        }

        byte[] pdfBytes = res.getInputStream().readAllBytes();
        String filename = res.getFilename() != null ? res.getFilename() : "platform.pdf";

        boolean ok = false;
        for (int i = 0; i < 6 && !ok; i++) {
            ok = botRepo.uploadPdf(pdfBytes, filename);
            if (!ok) Thread.sleep(1000);
        }
    }
}