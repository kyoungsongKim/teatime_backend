package castis.util.holiday;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class HolidayDto {
    private String name;
    private String date;

    public HolidayDto(HolidayJsonDto dto) {
        this.name = dto.getDateName();
        this.date = String.valueOf(dto.getLocdate());
    }
}
