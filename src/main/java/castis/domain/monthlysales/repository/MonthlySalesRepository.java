package castis.domain.monthlysales.repository;

import castis.domain.monthlysales.entity.MonthlySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MonthlySalesRepository
                extends JpaRepository<MonthlySales, Long>, JpaSpecificationExecutor<MonthlySales> {

        List<MonthlySales> findAllByUser_IdAndSummaryDateGreaterThanEqualAndSummaryDateLessThan(String userId,
                        LocalDateTime startDate, LocalDateTime endDate);

        @Query(value = "SELECT YEAR(summaryDate) as year FROM monthly_sales WHERE userid = ?1 GROUP BY YEAR(summaryDate)", nativeQuery = true)
        List<Integer> findAllYearByUser_Id(String userId);
}
