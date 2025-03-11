package castis.domain.aichatbot.service;

import castis.domain.aichatbot.entity.AiSupportHistory;
import castis.domain.aichatbot.repository.AiSupportHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiSupportService {
    private final AiSupportHistoryRepository aiSupportHistoryRepository;

    public void postAiSupportHistory(AiSupportHistory aiChatBotHistory) {
        aiSupportHistoryRepository.save(aiChatBotHistory);
    }
}
