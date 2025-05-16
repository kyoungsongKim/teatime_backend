package castis.domain.aichatbot.repository;

import castis.domain.aichatbot.entity.AiSupportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AiSupportHistoryRepository extends JpaRepository<AiSupportHistory, Long> {
    List<AiSupportHistory> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
