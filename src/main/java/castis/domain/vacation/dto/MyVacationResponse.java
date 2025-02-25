package castis.domain.vacation.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MyVacationResponse extends VacationInfoDto {
    List<VacationHistoryDto> histories;
    List<Byte> workedYearList;

}
