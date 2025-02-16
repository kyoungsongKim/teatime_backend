package castis.domain.user.dto;

import castis.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

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
    private String teamName;
    private String position;
    private String cellphone;
    private String email;
    private String dailyReportList;
    private LocalDate renewalDate;
    private String vacationReportList;

    public UserDto(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.realName = user.getRealName();
        this.teamName = user.getTeamName();
        this.position = user.getPosition();
        this.cellphone = user.getCellphone();
        this.email = user.getEmail();
        this.dailyReportList = user.getDailyReportList();
        this.renewalDate = user.getRenewalDate();
        this.vacationReportList = user.getVacationReportList();
    }
}