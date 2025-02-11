package castis.domain.assistance.controller;

import castis.domain.assistance.dto.AssistanceGroupDto;
import castis.domain.assistance.entity.AssistanceGroup;
import castis.domain.assistance.service.AssistanceGroupService;
import castis.domain.security.jwt.AuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/assistance/group")
public class AssistanceGroupController {

    private final AuthProvider authProvider;

    private final AssistanceGroupService assistanceGroupService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<AssistanceGroupDto>> getAssistanceGroupList() {
        List<AssistanceGroupDto> groups = assistanceGroupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<AssistanceGroupDto> createAssistanceGroup(@RequestBody AssistanceGroup group) {
        AssistanceGroupDto createdGroup = assistanceGroupService.createGroup(group);
        return ResponseEntity.ok(createdGroup);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<AssistanceGroupDto> updateAssistanceGroup(@PathVariable Integer id, @RequestBody AssistanceGroup group) {
        AssistanceGroupDto updatedGroup = assistanceGroupService.updateGroup(id, group);
        return ResponseEntity.ok(updatedGroup);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAssistanceGroup(@PathVariable Integer id) {
        assistanceGroupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }
}
