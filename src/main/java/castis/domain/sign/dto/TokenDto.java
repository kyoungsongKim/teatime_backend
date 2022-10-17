package castis.domain.sign.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class TokenDto {

    @NotBlank(message = "'apiToken' is a required input value")
    private String apiToken;

}
