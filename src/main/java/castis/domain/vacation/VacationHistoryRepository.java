package castis.domain.vacation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VacationHistoryRepository extends JpaRepository<VacationHistory, Long> {

    @Query(value = "DELETE FROM vacation_history WHERE ticketNo = :no AND status = :status" , nativeQuery = true)
    void deleteByTicketNo(Long no, String status);
}
