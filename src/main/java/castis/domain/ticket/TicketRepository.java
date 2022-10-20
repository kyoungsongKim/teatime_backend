package castis.domain.ticket;

import castis.domain.statistics.MonthlyStatisticsInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    @Query(value = "SELECT MONTH(t.starttime) as month, p.site as site, t.projectname as projectName, sum(t.emd) as sum, p.bgcolor as bgColor" +
            " FROM ticket as t LEFT JOIN project as p on t.projectname = p.projectname " +
            "where t.username = :userName and t.starttime > :sdate and t.starttime <= :edate group by month, t.projectname;" , nativeQuery = true)
    List<MonthlyStatisticsInterface> findAllMonthlySumMdByProject (String userName, String sdate, String edate);
}
