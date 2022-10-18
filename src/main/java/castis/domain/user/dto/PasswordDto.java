package castis.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class PasswordDto {

    @NotBlank(message = "'id' is a required input value")
    private String id;

    @NotBlank(message = "'old password' is a required input value")
    private String oldPassword;

    @NotBlank(message = "'new password' is a required input value")
    private String newPassword;

}
