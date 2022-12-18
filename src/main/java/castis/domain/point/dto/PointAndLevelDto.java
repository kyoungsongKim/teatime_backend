package castis.domain.point.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PointAndLevelDto {
    private int point;
    private int level;
    private int expvalue;

    public PointAndLevelDto(int point, int level, int expvalue) {
        this.point = point;
        this.level = level;
        this.expvalue = expvalue;
    }
}
