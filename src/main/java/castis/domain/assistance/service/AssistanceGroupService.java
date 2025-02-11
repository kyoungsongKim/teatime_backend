package castis.domain.assistance.service;

import castis.domain.assistance.dto.AssistanceDto;
import castis.domain.assistance.dto.AssistanceGroupDto;
import castis.domain.assistance.entity.AssistanceGroup;
import castis.domain.assistance.repository.AssistanceGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssistanceGroupService {

    private final AssistanceGroupRepository assistanceGroupRepository;

    @Transactional
    public List<AssistanceGroupDto> getAllGroups() {
        List<AssistanceGroup> groups = assistanceGroupRepository.findAll();
        return groups.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public AssistanceGroupDto createGroup(AssistanceGroup group) {
        AssistanceGroup savedGroup = assistanceGroupRepository.save(group);
        return convertToDto(savedGroup);
    }

    @Transactional
    public AssistanceGroupDto updateGroup(Integer id, AssistanceGroup group) {
        AssistanceGroup foundGroup = assistanceGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + id));
        group.setName(group.getName());
        group.setOrder(group.getOrder());
        AssistanceGroup updatedGroup = assistanceGroupRepository.save(group);
        return convertToDto(updatedGroup);
    }

    @Transactional
    public void deleteGroup(Integer id) {
        AssistanceGroup group = assistanceGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + id));
        assistanceGroupRepository.delete(group);
    }

    // DTO 변환 메서드
    private AssistanceGroupDto convertToDto(AssistanceGroup group) {
        List<AssistanceDto> serviceNames = group.getServices().stream().map(AssistanceDto::new).collect(Collectors.toList());
        return new AssistanceGroupDto(group.getId(), group.getName(), group.getOrder(), serviceNames);
    }
}
