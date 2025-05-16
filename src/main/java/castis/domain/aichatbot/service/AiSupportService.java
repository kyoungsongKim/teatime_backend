package castis.domain.aichatbot.service;

import castis.domain.aichatbot.entity.AiSupportHistory;
import castis.domain.aichatbot.repository.AiSupportHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiSupportService {
    private final AiSupportHistoryRepository aiSupportHistoryRepository;

    public void postAiSupportHistory(AiSupportHistory aiChatBotHistory) {
        aiSupportHistoryRepository.save(aiChatBotHistory);
    }

    public List<AiSupportHistory> getHistoriesFromYesterday() {
        LocalDateTime start = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime end = LocalDate.now().atStartOfDay();
        return aiSupportHistoryRepository.findAllByCreatedAtBetween(start, end);
    }
}
