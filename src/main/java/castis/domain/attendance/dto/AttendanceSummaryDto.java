package castis.domain.attendance.dto;

import castis.domain.attendance.entity.WorkType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceSummaryDto {
    private String userId;
    private String realName;
    private LocalDate workDate;
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private WorkType workType;
}