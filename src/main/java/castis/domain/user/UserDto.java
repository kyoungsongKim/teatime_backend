package castis.domain.user;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * A DTO for the {@link User} entity
 */
@Setter
@Getter
@NoArgsConstructor
public class UserDto implements Serializable {
    private String id;
    private String userName;
    private String realName;
    private Boolean enabled;
    private String teamName;
    private String position;
    private String cellphone;
    private String email;
    private String dailyReportList;
    private String vacationReportList;

    public UserDto(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.enabled = user.getEnabled();
        this.teamName = user.getTeamName();
        this.position = user.getPosition();
        this.cellphone = user.getCellphone();
        this.email = user.getEmail();
        this.dailyReportList = user.getDailyReportList();
        this.vacationReportList = user.getVacationReportList();
    }
}