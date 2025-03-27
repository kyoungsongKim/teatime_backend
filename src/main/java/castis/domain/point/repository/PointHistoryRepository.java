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
    Optional<List<PointHistory>> findAllByRecverOrderByCreateDateDesc(String recver);
    Optional<List<PointHistory>> findAllByRecverAndCreateDateAndCode(String recver, LocalDateTime date, String code);
    Optional<List<PointHistory>> findByCodeAndRecver(String code,String recver);
    @Query("select p from PointHistory p where year(p.useDate) = ?1 and month(p.useDate) = ?2")
    List<PointHistory> findAllByYearAndMonth(Integer year, Integer month);
}
