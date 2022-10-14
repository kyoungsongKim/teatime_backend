package castis.domain.sign.dto;

import castis.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class JoinDto {

    @NotBlank(message = "'id' is a required input value")
    private String id;

    @NotBlank(message = "'name' is a required input value")
    private String name;

    @NotBlank(message = "'password' is a required input value")
    private String password;

    @NotBlank(message = "'team' is a required input value")
    private String team;

    private String position;

    @NotBlank(message = "'phone' is a required input value")
    private String phone;

    @NotBlank(message = "'email' is a required input value")
    @Email(message = "do not email type")
    private String email;

    private String reportEmail;

    private String vacationReportList;

    public User toEntity() {
        User build = User.builder()
                .id(id)
                .userName(id)
                .realName(name)
                .password(password)
                .teamName(team)
                .position(position == null ? "" : position)
                .cellphone(phone == null ? "" : position)
                .email(email)
                .dailyReportList(reportEmail == null ? "" : reportEmail)
                .vacationReportList(reportEmail == null ? "" : reportEmail)
                .build();

        return build;
    }

}
