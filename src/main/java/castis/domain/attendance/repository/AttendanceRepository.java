package castis.domain.attendance.repository;

import castis.domain.attendance.entity.Attendance;
import castis.domain.attendance.entity.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
}
