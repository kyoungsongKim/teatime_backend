package castis.domain.assistance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import castis.domain.assistance.dto.AssistanceDto;
import castis.domain.assistance.entity.Assistance;
import castis.domain.assistance.repository.AssistanceRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class AssistanceService {

    private final AssistanceRepository assistanceRepository;

    public List<AssistanceDto> getAssistanceList() {
        List<Assistance> serviceList = assistanceRepository.findAll();
        List<AssistanceDto> result = serviceList.stream().map(AssistanceDto::new)
                .collect(Collectors.toList());
        return result;
    }

    public Assistance getAssistance(int id) {
        Assistance result = assistanceRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return result;
    }

}
