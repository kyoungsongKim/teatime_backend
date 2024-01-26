package castis.domain.assistance.dto;

import castis.domain.assistance.entity.AssistanceReview;
import castis.domain.user.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AssistanceReviewDto {
    private Integer id;

    private Integer rating;

    private String content;

    private UserDto reviewer;

    public AssistanceReviewDto(AssistanceReview assistanceReview) {
        this.id = assistanceReview.getId();
        this.rating = assistanceReview.getRating();
        this.content = assistanceReview.getContent();
        if (assistanceReview.getReviewer() != null) {
            this.reviewer = new UserDto(assistanceReview.getReviewer());
        }
    }
}
