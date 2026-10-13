package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.ChatMessage;
import pt.clutchmetrics.entity.User;

import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Long> {

    @EntityGraph(attributePaths = "user")
    List<ChatMessage> findByUserOrderByCreatedAtAsc(
            User user
    );
}