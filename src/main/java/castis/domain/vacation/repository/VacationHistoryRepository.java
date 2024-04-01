package castis.domain.vacation.repository;

import castis.domain.vacation.entity.IVacationInfo;
import castis.domain.vacation.entity.VacationHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface VacationHistoryRepository
                extends JpaRepository<VacationHistory, Long>, JpaSpecificationExecutor<VacationHistory> {

        @Query(nativeQuery = true, value = "SELECT * FROM vacation_history WHERE userId = ?1 AND ?2 <= IF(?4, DATE_ADD(eventStartDate, INTERVAL amount DIV 1 DAY), eventStartDate) AND eventStartDate <= ?3")
        List<VacationHistory> findAllByUserIdAndBetweenDateRange(String userId, LocalDateTime startDate,
                        LocalDateTime endDate, boolean includeAmount);

        @Query(nativeQuery = true, value = "SELECT * FROM vacation_history WHERE ?1 <= IF(?3, DATE_ADD(eventStartDate, INTERVAL amount DIV 1 DAY), eventStartDate) AND eventStartDate <= ?2")
        List<VacationHistory> findAllByBetweenDateRange(LocalDateTime startDate,
                        LocalDateTime endDate, boolean includeAmount);

        @Query(nativeQuery = true, value = "SELECT userId, (SELECT SUM(amount) FROM vacation_history NATURAL JOIN users WHERE (DATE_FORMAT(renewaldate, CONCAT(YEAR(?1) - 1 + (DATE_FORMAT(renewaldate, CONCAT(YEAR(?1), '-%m-%d %T')) >= ?1), '-%m-%d %T')) <= IF(?2, DATE_ADD(eventStartDate, INTERVAL amount DIV 1 DAY), eventStartDate) AND eventStartDate <= DATE_FORMAT(renewaldate, CONCAT(YEAR(?1) + (DATE_FORMAT(renewaldate, CONCAT(YEAR(?1), '-%m-%d %T')) >= ?1), '-%m-%d %T')))) as used, renewaldate FROM users")
        List<IVacationInfo> findAllVacationInfo(LocalDate targetDate, boolean includeAmount);

        @Query(nativeQuery = true, value = "SELECT userId, (SELECT SUM(amount) FROM vacation_history NATURAL JOIN users WHERE userId = ?1 AND (DATE_FORMAT(renewaldate, CONCAT(YEAR(?2) - 1 + (DATE_FORMAT(renewaldate, CONCAT(YEAR(?2), '-%m-%d %T')) >= ?2), '-%m-%d %T')) <= IF(?3, DATE_ADD(eventStartDate, INTERVAL amount DIV 1 DAY), eventStartDate) AND eventStartDate <= DATE_FORMAT(renewaldate, CONCAT(YEAR(?2) + (DATE_FORMAT(renewaldate, CONCAT(YEAR(?2), '-%m-%d %T')) >= ?2), '-%m-%d %T')))) as used, renewaldate FROM users WHERE userid = ?1")
        Optional<IVacationInfo> findVacationInfo(String userId, LocalDate targetDate, boolean includeAmount);

}
