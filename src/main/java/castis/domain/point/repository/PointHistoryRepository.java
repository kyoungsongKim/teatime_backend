package castis.domain.point.repository;

import castis.domain.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long>, JpaSpecificationExecutor<PointHistory> {

    Optional<List<PointHistory>> findAllByRecver(String recver);

    Optional<List<PointHistory>> findAllByRecverAndCreateDateAndCode(String recver, LocalDateTime date, String code);
    Optional<List<PointHistory>> findByCodeEquals(String code);
    @Query("select p from PointHistory p where year(p.createDate) = ?1 and month(p.createDate) = ?2")
    List<PointHistory> findAllByYearAndMonth(Integer year, Integer month);
}
