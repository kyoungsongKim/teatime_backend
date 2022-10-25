package castis.domain.point;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PointAndLevelDto {
    private int point;
    private int level;

    public PointAndLevelDto(int point, int level) {
        this.point = point;
        this.level = level;
    }
}
