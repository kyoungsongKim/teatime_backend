package castis.domain.assistance.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import castis.domain.assistance.constant.AppliedAssistanceStatus;
import castis.domain.assistance.dto.AssistanceApplyDto;
import castis.domain.assistance.entity.AssistanceApply;
import castis.domain.assistance.entity.AssistanceReview;
import castis.domain.assistance.repository.AssistanceApplyRepository;
import castis.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class AssistanceApplyService {

    private final AssistanceApplyRepository assistanceApplyRepository;
    private final AssistanceReviewService assistanceReviewService;

    public List<AssistanceApplyDto> getAssistanceApplyList() {
        List<AssistanceApply> serviceList = assistanceApplyRepository
                .findAll(Sort.by(Sort.Direction.DESC, "updatedDate"));
        List<AssistanceApplyDto> result = serviceList.stream().map(AssistanceApplyDto::new)
                .collect(Collectors.toList());
        return result;
    }

    public List<AssistanceApplyDto> getAssistanceApplyListByApplierId(String applierId) {
        List<AssistanceApply> serviceList = assistanceApplyRepository
                .findAllByApplierIdOrderByUpdatedDateDesc(applierId);
        List<AssistanceApplyDto> result = serviceList.stream().map(AssistanceApplyDto::new)
                .collect(Collectors.toList());
        return result;
    }

    public AssistanceApply getAssistanceApply(int id) {
        AssistanceApply result = assistanceApplyRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return result;
    }

    public AssistanceApplyDto applyAssistance(AssistanceApply assistanceApply) {
        AssistanceApply result = assistanceApplyRepository.save(assistanceApply);
        return new AssistanceApplyDto(result);
    }

    public AssistanceApplyDto changeAssistanceApplyStatus(int assistanceApplyId, AppliedAssistanceStatus status) {
        AssistanceApply assistanceApply = getAssistanceApply(assistanceApplyId);
        assistanceApply.setStatus(status);
        AssistanceApply result = assistanceApplyRepository.save(assistanceApply);
        return new AssistanceApplyDto(result);
    }

    public AssistanceApplyDto receiveAssistanceApply(int assistanceApplyId, String receiverId) {
        AssistanceApply assistanceApply = getAssistanceApply(assistanceApplyId);
        User receiver = new User();
        receiver.setId(receiverId);
        assistanceApply.setReceiver(receiver);
        assistanceApply.setStatus(AppliedAssistanceStatus.IN_PROGRESS);
        AssistanceApply result = assistanceApplyRepository.save(assistanceApply);
        return new AssistanceApplyDto(result);
    }

    public AssistanceApplyDto reviewAssistanceApply(int assistanceApplyId, AssistanceReview assistanceReview)
            throws IllegalArgumentException {
        AssistanceApply assistanceApply = getAssistanceApply(assistanceApplyId);
        System.out.println("@@@@= " + assistanceApply.getReview());
        if (assistanceApply.getReview() != null) {
            throw new IllegalArgumentException("review already exist");
        }
        assistanceApply.setReview(assistanceReview);
        assistanceReviewService.createAssistanceReview(assistanceReview);
        AssistanceApply result = assistanceApplyRepository.save(assistanceApply);
        return new AssistanceApplyDto(result);
    }
}
