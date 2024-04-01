package castis.domain.vacation.dto;

import java.time.LocalDate;

import castis.domain.vacation.entity.IVacationInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VacationInfoDto {

    private String userId;
    private Float left;
    private Float used;
    private Float total;
    private LocalDate renewalDate;

    public VacationInfoDto(IVacationInfo vacationInfo) {
        this.userId = vacationInfo.getUserId();
        this.left = vacationInfo.getLeft();
        this.used = vacationInfo.getUsed();
        this.total = vacationInfo.getTotal();
        this.renewalDate = vacationInfo.getRenewalDate();
    }
}
