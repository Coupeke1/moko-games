package be.kdg.team22.communicationservice.infrastructure.chat.jpa;

import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface JpaChatChannelRepository extends JpaRepository<ChannelEntity, UUID> {
    Optional<ChannelEntity> findByTypeAndReferenceId(ChatChannelType type, String referenceId);
}