package castis.domain.attendance.service;

import castis.domain.attendance.dto.AttendanceDto;
import castis.domain.attendance.entity.Attendance;
import castis.domain.attendance.entity.WorkType;
import castis.domain.attendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public List<Attendance> getAttendance(String userId, LocalDate workDate, LocalDate endDate) {
        if (userId != null && workDate != null && endDate != null) {
            return attendanceRepository.findByUserIdAndWorkDateBetween(userId, workDate, endDate);
        } else if (userId != null && workDate != null) {
            return attendanceRepository.findByUserIdAndWorkDate(userId, workDate);
        } else if (userId != null) {
            return attendanceRepository.findByUserId(userId);
        } else if (workDate != null) {
            return attendanceRepository.findByWorkDate(workDate);
        } else {
            LocalDate threeMonthsAgo = LocalDate.now().minus(3, ChronoUnit.MONTHS);
            return attendanceRepository.findByWorkDateAfter(threeMonthsAgo);
        }
    }

    public Attendance saveAttendance(AttendanceDto attendanceDto) {
        try {
            Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndWorkTypeAndWorkDate(
                    attendanceDto.getUserId(),
                    WorkType.valueOf(attendanceDto.getWorkType()),
                    LocalDate.now()
            );

            if (existingAttendance.isPresent()) {
                Attendance attendance = existingAttendance.get();
                if ("startTime".equalsIgnoreCase(attendanceDto.getTimeType())) {
                    attendance.setWorkStartTime(LocalTime.now());
                } else if ("endTime".equalsIgnoreCase(attendanceDto.getTimeType())) {
                    attendance.setWorkEndTime(LocalTime.now());
                }
                attendance.setLocation(attendanceDto.getLocation());
                attendance.setManagerName(attendanceDto.getManagerName());
                attendance.setTaskDescription(attendanceDto.getTaskDescription());
                return attendanceRepository.save(attendance);
            }

            Attendance newAttendance = attendanceDto.toEntity();
            return attendanceRepository.save(newAttendance);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Failed to save attendance: " + e.getMessage(), e);
        }
    }
}
