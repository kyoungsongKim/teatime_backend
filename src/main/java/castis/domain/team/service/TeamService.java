package castis.domain.team.service;

import castis.domain.team.dto.TeamDto;
import castis.domain.team.entity.Team;
import castis.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    public List<TeamDto> findAllTeam() {
        List<TeamDto> teamDtoList = new ArrayList<>();
        List<Team> teamList = teamRepository.findAll();
        for (Team team : teamList) {
            TeamDto teamDto = new TeamDto(team);
            teamDtoList.add(teamDto);
        }
        return teamDtoList;
    }
}
