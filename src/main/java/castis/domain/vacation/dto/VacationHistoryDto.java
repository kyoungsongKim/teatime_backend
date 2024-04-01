package castis.domain.vacation.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import castis.domain.vacation.entity.VacationHistory;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VacationHistoryDto {

    private Long id;

    private String userId;

    private LocalDateTime eventStartDate;

    private LocalDateTime eventEndDate;

    private float amount;

    private String type;

    private String reason;

    @JsonInclude(Include.NON_NULL)
    private String adminMemo;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    public VacationHistoryDto(VacationHistory vacationHistory) {
        this.id = vacationHistory.getId();
        this.userId = vacationHistory.getUserId();
        this.eventStartDate = vacationHistory.getEventStartDate();
        this.eventEndDate = vacationHistory.getEventEndDate();
        this.amount = vacationHistory.getAmount();
        this.type = vacationHistory.getType();
        this.reason = vacationHistory.getReason();
        this.adminMemo = vacationHistory.getAdminMemo();
        this.createdDate = vacationHistory.getCreatedDate();
        this.updatedDate = vacationHistory.getUpdatedDate();
    }
}
