package castis.domain.team.dto;

import castis.domain.team.entity.Team;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link Team} entity
 */
@Data
@Getter
@Setter
public class TeamDto implements Serializable {
    @Size(max = 128)
    private String teamName;
    @NotNull
    private String teamDescription;

    public TeamDto(Team team) {
        this.teamName = team.getTeamName();
        this.teamDescription = team.getTeamDescription();
    }
}