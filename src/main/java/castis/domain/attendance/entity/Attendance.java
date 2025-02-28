package castis.domain.attendance.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id")
    private Long id;

    @Column(name = "userid", nullable = false, length = 128)
    private String userId;  // 사용자 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", length = 20)
    private WorkType workType;  // 근무 유형 (OFFICE, REMOTE, FIELD)

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;  // 근무 날짜

    @Column(name = "work_start_time")
    private LocalTime workStartTime;  // 출근 시간

    @Column(name = "work_end_time")
    private LocalTime workEndTime;  // 퇴근 시간

    @Column(name = "location", length = 100)
    private String location;  // 근무 위치

    @Column(name = "manager_name", length = 128)
    private String managerName;  // 관리자 ID

    @Column(name = "task_description", columnDefinition = "TEXT")
    private String taskDescription;  // 업무 내용

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;  // 기타 설명
}