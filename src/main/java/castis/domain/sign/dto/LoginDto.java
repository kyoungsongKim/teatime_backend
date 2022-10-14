package castis.domain.sign.dto;

import castis.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class LoginDto {

    @NotBlank(message = "'userId' is a required input value")
    private String userId;

    @NotBlank(message = "'password' is a required input value")
    private String password;


    public User toEntity() {
        User build = User.builder()
                .id(userId)
                .password(password)
                .build();

        return build;
    }

}
