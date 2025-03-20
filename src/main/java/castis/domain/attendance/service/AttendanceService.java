package castis.domain.attendance.service;

import castis.domain.attendance.dto.AttendanceDto;
import castis.domain.attendance.dto.AttendanceSummaryDto;
import castis.domain.attendance.entity.Attendance;
import castis.domain.attendance.entity.WorkType;
import castis.domain.attendance.repository.AttendanceRepository;
import castis.domain.user.entity.User;
import castis.domain.user.repository.UserRepository;
import castis.domain.vacation.entity.VacationHistory;
import castis.domain.vacation.repository.VacationHistoryRepository;
import castis.util.holiday.HolidayDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final VacationHistoryRepository vacationHistoryRepository;

    public List<Attendance> getAttendance(String userId, LocalDate workDate, LocalDate endDate) {
        if (userId != null && workDate != null && endDate != null) {
            return attendanceRepository.findByUserIdAndWorkDateBetweenOrderByWorkDateDescWorkStartTimeDesc(userId, workDate, endDate);
        } else if (userId != null && workDate != null) {
            return attendanceRepository.findByUserIdAndWorkDateOrderByWorkDateDescWorkStartTimeDesc(userId, workDate);
        } else if (userId != null) {
            return attendanceRepository.findByUserIdOrderByWorkDateDescWorkStartTimeDesc(userId);
        } else if (workDate != null) {
            return attendanceRepository.findByWorkDateOrderByWorkDateDescWorkStartTimeDesc(workDate);
        } else {
            LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
            return attendanceRepository.findByWorkDateAfterOrderByWorkDateDescWorkStartTimeDesc(threeMonthsAgo);
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

    public List<Map<String, Object>> getMonthlyAttendance(int year, int month, List<HolidayDto> holidays, String teamName) {
        List<User> allUsers;
        // teamName이 공백이 아니면 해당 팀 사용자만 조회
        if (teamName != null && !teamName.trim().isEmpty()) {
            allUsers = userRepository.findByTeamName(teamName);
        } else {
            allUsers = userRepository.findAll(); // 전체 사용자 조회
        }
        List<AttendanceSummaryDto> attendanceRecords = attendanceRepository.findByMonthWithUser(year, month, teamName);
        List<VacationHistory> vacationRecords = vacationHistoryRepository.findByMonth(year, month, teamName);

        // 공휴일을 Set으로 저장 (빠른 검색을 위해)
        Set<LocalDate> holidaySet = holidays.stream()
                .map(h -> LocalDate.parse(h.getDate(), DateTimeFormatter.BASIC_ISO_DATE)) // "yyyyMMdd" 포맷 변환
                .collect(Collectors.toSet());

        // 모든 사용자 기본 데이터 초기화
        Map<String, Map<String, String>> userAttendanceMap = new HashMap<>();
        Map<String, String> userRealNameMap = new HashMap<>();

        // 휴가 정보 맵핑
        Map<String, Map<String, String>> userVacationMap = new HashMap<>();
        for (VacationHistory vacation : vacationRecords) {
            String userId = vacation.getUserId();
            LocalDate startDate = vacation.getEventStartDate().toLocalDate();
            LocalDate endDate = vacation.getEventEndDate().toLocalDate();

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                String dateKey = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
                userVacationMap
                        .computeIfAbsent(userId, k -> new LinkedHashMap<>())
                        .put(dateKey, "VACATION");
            }
        }

        // 모든 사용자에 대해 근태 데이터 초기화
        for (User user : allUsers) {
            userRealNameMap.put(user.getId(), user.getRealName());

            Map<String, String> defaultAttendance = new LinkedHashMap<>();
            LocalDate firstDay = LocalDate.of(year, month, 1);
            int daysInMonth = firstDay.lengthOfMonth();

            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate currentDate = firstDay.withDayOfMonth(day);
                String dateKey = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

                // 공휴일 또는 주말 기본값 설정
                if (holidaySet.contains(currentDate) || currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    defaultAttendance.put(dateKey, "HOLIDAY");
                } else {
                    defaultAttendance.put(dateKey, "ABSENT"); // 기본 결근 처리
                }
            }
            userAttendanceMap.put(user.getId(), defaultAttendance);
        }

        // 출근 기록을 날짜별로 그룹화 (같은 날 여러 개의 WorkType을 처리하기 위해)
        Map<String, Map<String, AttendanceSummaryDto>> groupedRecords = new HashMap<>();

        for (AttendanceSummaryDto record : attendanceRecords) {
            String dateKey = record.getWorkDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            groupedRecords.computeIfAbsent(record.getUserId(), k -> new HashMap<>());

            // 기존에 등록된 값이 있는지 확인
            AttendanceSummaryDto existingRecord = groupedRecords.get(record.getUserId()).get(dateKey);

            if (existingRecord == null || isHigherPriority(record.getWorkType(), existingRecord.getWorkType())) {
                groupedRecords.get(record.getUserId()).put(dateKey, record);
            }
        }

        // 출근 기록 반영 (우선순위 적용)
        for (Map.Entry<String, Map<String, AttendanceSummaryDto>> userEntry : groupedRecords.entrySet()) {
            String userId = userEntry.getKey();
            for (Map.Entry<String, AttendanceSummaryDto> entry : userEntry.getValue().entrySet()) {
                String dateKey = entry.getKey();
                AttendanceSummaryDto record = entry.getValue();
                String status = getAttendanceStatus(record);

                userAttendanceMap
                        .computeIfAbsent(userId, k -> new LinkedHashMap<>())
                        .put(dateKey, status);
            }
        }

        // 휴가 데이터 추가 (공휴일 또는 주말이면 적용하지 않음)
        for (Map.Entry<String, Map<String, String>> vacationEntry : userVacationMap.entrySet()) {
            String userId = vacationEntry.getKey();
            Map<String, String> vacationData = vacationEntry.getValue();

            for (Map.Entry<String, String> vacationEntryData : vacationData.entrySet()) {
                String dateKey = vacationEntryData.getKey();
                LocalDate currentDate = LocalDate.parse(dateKey);

                // 공휴일 또는 주말이면 무시하고 유지
                if (!(holidaySet.contains(currentDate) || currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                    userAttendanceMap
                            .computeIfAbsent(userId, k -> new LinkedHashMap<>())
                            .put(dateKey, "VACATION");
                }
            }
        }

        // 최종 데이터 반환 (이름순 정렬)
        return userAttendanceMap.entrySet().stream()
                .sorted(Comparator.comparing(e -> userRealNameMap.getOrDefault(e.getKey(), ""))) // 이름순 정렬
                .map(entry -> {
                    Map<String, Object> userAttendance = new LinkedHashMap<>();
                    userAttendance.put("userId", entry.getKey());
                    userAttendance.put("realName", userRealNameMap.get(entry.getKey()));
                    userAttendance.put("year", year);
                    userAttendance.put("month", month);
                    userAttendance.put("attendanceStatus", entry.getValue());
                    return userAttendance;
                })
                .collect(Collectors.toList());
    }

    private String getAttendanceStatus(AttendanceSummaryDto record) {
        if (record.getWorkStartTime() == null && record.getWorkEndTime() == null) {
            return "ABSENT";
        }

        String workTypeStatus;
        switch (record.getWorkType()) {
            case OFFICE:
                workTypeStatus = "PRESENT";
                break;
            case REMOTE:
                workTypeStatus = "REMOTE";
                break;
            case FIELD:
                workTypeStatus = "FIELD";
                break;
            default:
                workTypeStatus = "UNKNOWN";
        }

        if (record.getWorkStartTime() != null && record.getWorkStartTime().isAfter(LocalTime.of(10, 0))) {
            return "LATE";
        }
        if (record.getWorkEndTime() != null && record.getWorkEndTime().isBefore(LocalTime.of(19, 0))) {
            return "EARLY_LEAVE";
        }
        return workTypeStatus;
    }

    private boolean isHigherPriority(WorkType newType, WorkType existingType) {
        List<WorkType> priority = Arrays.asList(WorkType.OFFICE, WorkType.REMOTE, WorkType.FIELD);
        return priority.indexOf(newType) < priority.indexOf(existingType);
    }
}
