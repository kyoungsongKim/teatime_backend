package castis.domain.assistance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import castis.domain.assistance.dto.AssistanceSuggestionDto;
import castis.domain.assistance.entity.AssistanceSuggestion;
import castis.domain.assistance.repository.AssistanceSuggestionRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class AssistanceSuggestionService {

    private final AssistanceSuggestionRepository assistanceSuggestionRepository;

    public List<AssistanceSuggestionDto> getAssistanceSuggestionList() {
        List<AssistanceSuggestion> serviceList = assistanceSuggestionRepository.findAll();
        List<AssistanceSuggestionDto> result = serviceList.stream().map(AssistanceSuggestionDto::new)
                .collect(Collectors.toList());
        return result;
    }

    public AssistanceSuggestion getAssistanceSuggestion(int id) {
        AssistanceSuggestion result = assistanceSuggestionRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        return result;
    }

    public AssistanceSuggestion createAssistanceSuggestion(AssistanceSuggestion assistanceSuggestion) {
        AssistanceSuggestion result = assistanceSuggestionRepository.save(assistanceSuggestion);
        return result;
    }

}
