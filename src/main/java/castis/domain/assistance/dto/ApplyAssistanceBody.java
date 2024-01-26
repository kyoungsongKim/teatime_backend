package castis.domain.assistance.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;

@Getter
public class ApplyAssistanceBody {

    @NotBlank(message = "'content' is a required property")
    private String content;

}
