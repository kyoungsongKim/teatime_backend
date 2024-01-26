package castis.domain.assistance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import castis.domain.assistance.dto.AssistanceReviewDto;
import castis.domain.assistance.entity.AssistanceReview;
import castis.domain.assistance.repository.AssistanceReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class AssistanceReviewService {

    private final AssistanceReviewRepository assistanceReviewRepository;

    public List<AssistanceReviewDto> getAssistanceReviewList() {
        List<AssistanceReview> serviceList = assistanceReviewRepository.findAll();
        List<AssistanceReviewDto> result = serviceList.stream().map(AssistanceReviewDto::new)
                .collect(Collectors.toList());
        return result;
    }

    public AssistanceReview getAssistanceReview(int id) {
        AssistanceReview result = assistanceReviewRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return result;
    }

    public AssistanceReview createAssistanceReview(AssistanceReview assistanceReview) {
        return assistanceReviewRepository.save(assistanceReview);
    }

}
