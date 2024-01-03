package castis.domain.summary.repository;

import castis.domain.summary.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long>, JpaSpecificationExecutor<Summary> {

    Optional<Summary> findByUserIdAndYearAndMonth(String userId, Integer year, Integer month);
}
