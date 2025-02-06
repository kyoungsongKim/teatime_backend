package castis.domain.usernavigationhistory.repository;

import castis.domain.usernavigationhistory.entity.UserNavigationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNavigationHistoryRepository extends JpaRepository<UserNavigationHistory, Long> {
    List<UserNavigationHistory> findByUserId(String userId);
    List<UserNavigationHistory> findByMenuName(String menuName);
}
