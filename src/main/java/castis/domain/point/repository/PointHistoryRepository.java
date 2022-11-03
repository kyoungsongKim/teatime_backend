package castis.domain.point.repository;

import castis.domain.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long>, JpaSpecificationExecutor<PointHistory> {

    Optional<List<PointHistory>> findAllByRecver(String recver);

    Optional<PointHistory> findByCode(String code);
}
