package castis.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class UserDetailDto {
    private String userId;
    private String avatarImg;
    private String realName;
    private String email;
    private String cellphone;
    private String address;
    private String position;
    private String teamName;
    private OffsetDateTime birthDate;
    private String cbankAccount;
    private String educationLevel;
    private String skillLevel;
    private String description;
    private String dailyReportList;
    private String vacationReportList;
}
