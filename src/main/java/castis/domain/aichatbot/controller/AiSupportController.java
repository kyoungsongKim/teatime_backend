package castis.domain.aichatbot.controller;

import castis.domain.aichatbot.entity.AiSupportHistory;
import castis.domain.aichatbot.service.AiSupportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/ai-support")
public class AiSupportController {

    private final AiSupportService aiSupportService;

    @PostMapping(value = "")
    @Transactional
    public ResponseEntity<Void> postAiSupportHistory(@RequestBody AiSupportHistory aiChatBotHistory) {
        try {
            aiSupportService.postAiSupportHistory(aiChatBotHistory);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
