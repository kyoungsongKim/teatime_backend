package castis.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class UserUpdateDto {

    @NotBlank(message = "'id' is a required input value")
    private String id;

    @NotBlank(message = "'name' is a required input value")
    private String name;

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
}
