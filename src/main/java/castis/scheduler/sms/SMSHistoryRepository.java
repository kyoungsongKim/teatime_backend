package castis.scheduler.sms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SMSHistoryRepository extends JpaRepository<SMSHistory, Long>, JpaSpecificationExecutor<SMSHistory> {
}
