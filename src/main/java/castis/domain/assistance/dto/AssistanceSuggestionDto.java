package castis.domain.assistance.dto;

import castis.domain.assistance.entity.AssistanceSuggestion;
import castis.domain.user.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AssistanceSuggestionDto {
    private Integer id;

    private String content;

    private UserDto suggester;

    public AssistanceSuggestionDto(AssistanceSuggestion assistanceSuggestion) {
        this.id = assistanceSuggestion.getId();
        this.content = assistanceSuggestion.getContent();
        if (assistanceSuggestion.getSuggester() != null) {
            this.suggester = new UserDto(assistanceSuggestion.getSuggester());
        }
    }
}
