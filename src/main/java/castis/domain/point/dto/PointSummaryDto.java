package castis.domain.point.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PointSummaryDto {
    private String userId;
    private String realName;
    private Integer totalPoint;
    private Integer totalExp;
    private Integer level;
}
