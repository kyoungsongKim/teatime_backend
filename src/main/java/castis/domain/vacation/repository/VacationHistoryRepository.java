package castis.domain.vacation.repository;

import castis.domain.vacation.entity.IVacationInfo;
import castis.domain.vacation.entity.VacationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VacationHistoryRepository extends JpaRepository<VacationHistory, Long>, JpaSpecificationExecutor<VacationHistory> {

        /**
         * 특정 사용자의 휴가 이력을 조회 (지정된 날짜 범위 내)
         * includeAmount 가 true이면 eventStartDate + amount 를 기준으로 조회
         */
        @Query(nativeQuery = true, value =
                "SELECT * FROM vacation_history " +
                        "WHERE userId = ?1 " +
                        "AND ?2 <= IF(?4, DATE_ADD(eventStartDate, INTERVAL amount DAY), eventStartDate) " +
                        "AND eventStartDate <= ?3")
        List<VacationHistory> findAllByUserIdAndBetweenDateRange(
                String userId, LocalDateTime startDate, LocalDateTime endDate, boolean includeAmount
        );

        /**
         * 모든 사용자의 휴가 이력을 조회 (지정된 날짜 범위 내)
         * includeAmount 가 true이면 eventStartDate + amount 를 기준으로 조회
         */
        @Query(nativeQuery = true, value =
                "SELECT * FROM vacation_history " +
                        "WHERE ?1 <= IF(?3, DATE_ADD(eventStartDate, INTERVAL amount DAY), eventStartDate) " +
                        "AND eventStartDate <= ?2")
        List<VacationHistory> findAllByBetweenDateRange(
                LocalDateTime startDate, LocalDateTime endDate, boolean includeAmount
        );

        /**
         * 전체 사용자에 대한 휴가 사용 정보 조회
         */
        @Query(nativeQuery = true, value =
                "SELECT u.userId, u.realname, v.used, d.renewaldate " +
                        "FROM users u " +
                        "JOIN user_details d ON u.userId = d.userId " +
                        "LEFT JOIN ( " +
                        "   SELECT SUM(v.amount) AS used, v.userId " +
                        "   FROM vacation_history v " +
                        "   JOIN user_details d ON v.userId = d.userId " +
                        "   WHERE ( " +
                        "       DATE_FORMAT(d.renewaldate, CONCAT(YEAR(?1) - 1 + " +
                        "       (DATE_FORMAT(d.renewaldate, '%Y-%m-%d 00:00:00') <= ?1), '-%m-%d 00:00:00')) <= " +
                        "       IF(?2, DATE_ADD(v.eventStartDate, INTERVAL amount DAY), v.eventStartDate) " +
                        "       AND v.eventStartDate < DATE_FORMAT(d.renewaldate, CONCAT(YEAR(?1) + " +
                        "       (DATE_FORMAT(d.renewaldate, '%Y-%m-%d 00:00:00') <= ?1), '-%m-%d 00:00:00')) " +
                        "   ) " +
                        "   GROUP BY v.userId " +
                        ") v ON u.userId = v.userId " +
                        "ORDER BY u.realname")
        List<IVacationInfo> findAllVacationInfo(LocalDateTime targetDate, boolean includeAmount);

        /**
         * 같은 팀 사용자에 대한 휴가 사용 정보 조회
         */
        @Query(nativeQuery = true, value =
                "SELECT u.userId, u.realname, COALESCE(v.used, 0) AS used, d.renewaldate " +
                        "FROM users u " +
                        "JOIN user_details d ON u.userId = d.userId " +
                        "LEFT JOIN ( " +
                        "   SELECT SUM(v.amount) AS used, v.userId " +
                        "   FROM vacation_history v " +
                        "   JOIN user_details d ON v.userId = d.userId " +
                        "   WHERE ( " +
                        "       DATE_FORMAT(d.renewaldate, CONCAT(YEAR(?1) - 1 + " +
                        "       (DATE_FORMAT(d.renewaldate, '%Y-%m-%d 00:00:00') <= ?1), '-%m-%d 00:00:00')) <= " +
                        "       IF(?2, DATE_ADD(v.eventStartDate, INTERVAL amount DAY), v.eventStartDate) " +
                        "       AND v.eventStartDate < DATE_FORMAT(d.renewaldate, CONCAT(YEAR(?1) + " +
                        "       (DATE_FORMAT(renewaldate, '%Y-%m-%d 00:00:00') <= ?1), '-%m-%d 00:00:00')) " +
                        "   ) " +
                        "   GROUP BY v.userId " +
                        ") v ON u.userId = v.userId " +
                        "WHERE u.teamname = ?3 " +
                        "ORDER BY u.realname")
        List<IVacationInfo> findAllVacationInfoByTeamName(LocalDateTime targetDate, boolean includeAmount, String teamName);

        /**
         * 특정 사용자의 휴가 사용 정보 조회
         */
        @Query(nativeQuery = true, value =
                "SELECT v.userId, SUM(v.amount) AS used, d.renewaldate " +
                        "FROM vacation_history v " +
                        "JOIN user_details d ON v.userId = d.userId " +
                        "WHERE v.userId = ?1 " +
                        "AND ( " +
                        "   DATE_FORMAT(d.renewaldate, CONCAT(YEAR(?2) - 1 + " +
                        "   (DATE_FORMAT(d.renewaldate, '%Y-%m-%d 00:00:00') <= ?2), '-%m-%d 00:00:00')) <= " +
                        "   IF(?3, DATE_ADD(v.eventStartDate, INTERVAL v.amount DAY), v.eventStartDate) " +
                        "   AND v.eventStartDate < DATE_FORMAT(d.renewaldate, CONCAT(YEAR(?2) + " +
                        "   (DATE_FORMAT(d.renewaldate, '%Y-%m-%d 00:00:00') <= ?2), '-%m-%d 00:00:00')) " +
                        ")")
        Optional<IVacationInfo> findVacationInfo(
                String userId, LocalDateTime targetDate, boolean includeAmount
        );

        /**
         * 특정 사용자의 특정 연도의 휴가 이력 조회
         */
        @Query(nativeQuery = true, value =
                "SELECT * FROM vacation_history " +
                        "WHERE userId = ?1 " +
                        "AND YEAR(eventStartDate) = ?2")
        List<VacationHistory> findAllByUserIdAndYear(String userId, int year);

        // 특정 월 모든 유저의 휴가 기록 조회 (teamName 필터 추가)
        @Query("SELECT v FROM VacationHistory v " +
                "JOIN User u ON v.userId = u.id " +
                "JOIN UserDetails d ON u.id = d.userId " +
                "WHERE YEAR(v.eventStartDate) = :year AND MONTH(v.eventStartDate) = :month " +
                "AND (:teamName IS NULL OR :teamName = '' OR u.teamName = :teamName)")
        List<VacationHistory> findByMonth(
                @Param("year") int year,
                @Param("month") int month,
                @Param("teamName") String teamName
        );
}