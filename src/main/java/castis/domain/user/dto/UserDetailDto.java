package castis.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

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
    private LocalDate birthDate;
    private String cbankAccount;
    private String educationLevel;
    private String skillLevel;
    private String description;
    private String dailyReportList;
    private String vacationReportList;
}
