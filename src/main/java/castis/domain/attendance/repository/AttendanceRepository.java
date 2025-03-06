package castis.domain.attendance.repository;

import castis.domain.attendance.dto.AttendanceSummaryDto;
import castis.domain.attendance.entity.Attendance;
import castis.domain.attendance.entity.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {
    List<Attendance> findByUserId(String userId);
    List<Attendance> findByWorkDate(LocalDate workDate);
    List<Attendance> findByUserIdAndWorkDate(String userId, LocalDate workDate);
    List<Attendance> findByWorkDateAfter(LocalDate minDate);
    Optional<Attendance> findByUserIdAndWorkTypeAndWorkDate(String userid, WorkType workType, LocalDate workDate);
    List<Attendance> findByUserIdAndWorkDateBetween(String userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT new castis.domain.attendance.dto.AttendanceSummaryDto(a.userId, u.realName, a.workDate, a.workStartTime, a.workEndTime, a.workType) " +
            "FROM Attendance a " +
            "JOIN User u ON a.userId = u.id " +
            "WHERE YEAR(a.workDate) = :year AND MONTH(a.workDate) = :month " +
            "AND (:teamName IS NULL OR :teamName = '' OR u.teamName = :teamName)")
    List<AttendanceSummaryDto> findByMonthWithUser(int year, int month, @Param("teamName") String teamName);
}
