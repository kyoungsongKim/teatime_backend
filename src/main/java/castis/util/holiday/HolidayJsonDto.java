package castis.util.holiday;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class HolidayJsonDto {
    private String dateKind;
    private String dateName;
    private String isHoliday;
    private Integer locdate;
    private String seq;
}
