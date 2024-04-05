package castis.domain.vacation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MyVacationResponse extends VacationInfoDto {
    List<VacationHistoryDto> histories;
    List<Byte> workedYearList;

}
