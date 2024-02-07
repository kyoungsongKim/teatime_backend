package castis.domain.team.controller;

import castis.domain.team.dto.TeamDto;
import castis.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class TeamController {

    private final TeamService teamService;

    @RequestMapping(value = "/team", method = RequestMethod.GET)
    public ResponseEntity getTeamList() {
        List<TeamDto> teamDtoList = teamService.findAllTeam();
        return new ResponseEntity<>(teamDtoList, HttpStatus.OK);
    }
}
