package castis.domain.attendance.controller;

import castis.domain.agreement.entity.IUserAgreementInfo;
import castis.domain.attendance.dto.AttendanceDto;
import castis.domain.attendance.entity.Attendance;
import castis.domain.attendance.service.AttendanceService;
import castis.domain.filesystem.service.FileSystemService;
import castis.domain.security.jwt.AuthProvider;
import castis.domain.user.CustomUserDetails;
import castis.domain.user.entity.User;
import castis.domain.user.service.UserService;
import castis.util.holiday.HolidayDto;
import castis.util.holiday.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
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
    private final AuthProvider authProvider;

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
    public ResponseEntity<Map<String, Object>> getMonthlyAttendance(HttpServletRequest httpServletRequest,
                                                                    @RequestParam int year,
                                                                    @RequestParam int month) throws IOException {

        List<HolidayDto> holidays = holidayService.getHolidayInfo(String.valueOf(year), String.format("%02d", month));

        boolean isAdmin = authProvider.isAdmin(httpServletRequest);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        CustomUserDetails userDetails = authProvider.getUserDetails(httpServletRequest);
        List<Map<String, Object>> attendanceSummaries;

        if (userDetails.getRoles().contains("ROLE_ADMIN")) {
            User user = userService.getUser(userDetails.getUserId());
            attendanceSummaries = attendanceService.getMonthlyAttendance(year, month, holidays, user.getTeamName());
        } else if (userDetails.getRoles().contains("ROLE_SUPER_ADMIN")) {
            attendanceSummaries = attendanceService.getMonthlyAttendance(year, month, holidays, "");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("attendanceData", attendanceSummaries);
        responseMap.put("holidays", holidays);

        return ResponseEntity.ok(responseMap);
    }
}
