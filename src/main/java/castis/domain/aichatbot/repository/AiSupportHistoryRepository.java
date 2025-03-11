package castis.domain.aichatbot.repository;

import castis.domain.aichatbot.entity.AiSupportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiSupportHistoryRepository extends JpaRepository<AiSupportHistory, Long> {
}
