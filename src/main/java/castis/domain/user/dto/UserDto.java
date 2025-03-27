package castis.domain.user.dto;

import castis.domain.user.entity.User;
import castis.domain.user.entity.UserDetails;
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
    private String description;
    private String teamName;
    private String position;
    private String cellphone;
    private String email;
    private String dailyReportList;
    private LocalDate renewalDate;
    private String vacationReportList;
    private UserDetails userDetails;

    public UserDto(User user) {
        this(user, true); // isDetail 기본값을 true로 설정
    }

    public UserDto(User user, Boolean isDetail) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.realName = user.getRealName();
        this.description = user.getDescription();
        this.teamName = user.getTeamName();
        this.position = user.getPosition();
        if(isDetail){
            this.userDetails = user.getUserDetails();
            if(user.getUserDetails() != null) {
                this.cellphone = user.getUserDetails().getCellphone();
                this.email = user.getUserDetails().getEmail();
                this.dailyReportList = user.getUserDetails().getDailyReportList();
                this.renewalDate = user.getUserDetails().getRenewalDate();
                this.vacationReportList = user.getUserDetails().getVacationReportList();
            }
        }
    }
}
