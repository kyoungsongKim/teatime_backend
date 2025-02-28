package castis.domain.attendance.dto;

import castis.domain.attendance.entity.Attendance;
import castis.domain.attendance.entity.WorkType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
public class AttendanceDto {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Work Type is required")
    private String workType;

    private String timeType;
    private String location;
    private String managerName;
    private String taskDescription;

    public Attendance toEntity() {
        Attendance attendance = new Attendance();
        attendance.setUserId(this.userId);
        attendance.setWorkDate(LocalDate.now());
        attendance.setLocation(this.location);
        attendance.setManagerName(this.managerName);
        attendance.setTaskDescription(this.taskDescription);
        attendance.setWorkType(WorkType.valueOf(this.workType));

        if ("startTime".equalsIgnoreCase(this.timeType)) {
            attendance.setWorkStartTime(LocalTime.now());
        } else if ("endTime".equalsIgnoreCase(this.timeType)) {
            attendance.setWorkEndTime(LocalTime.now());
        }

        return attendance;
    }
}
