package castis.domain.attendance.controller;

import castis.domain.attendance.dto.AttendanceDto;
import castis.domain.attendance.entity.Attendance;
import castis.domain.attendance.service.AttendanceService;
import castis.domain.user.service.UserService;
import castis.util.holiday.HolidayDto;
import castis.util.holiday.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final HolidayService holidayService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Attendance>> getAttendance(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(attendanceService.getAttendance(userId, workDate, endDate));
    }

    @PostMapping
    public ResponseEntity<Attendance> saveAttendance(@RequestBody AttendanceDto attendanceDto) {
        return ResponseEntity.ok(attendanceService.saveAttendance(attendanceDto));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyAttendance(
            @RequestParam int year,
            @RequestParam int month) throws IOException {

        List<HolidayDto> holidays = holidayService.getHolidayInfo(String.valueOf(year), String.valueOf(month));

        List<Map<String, Object>> attendanceSummaries = attendanceService.getMonthlyAttendance(year, month, holidays);
        return ResponseEntity.ok(attendanceSummaries);
    }
}
