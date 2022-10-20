package castis.domain.statistics;

import castis.domain.ticket.EventDto;
import castis.domain.ticket.Ticket;
import castis.domain.ticket.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final TicketRepository ticketRepository;

    public Map<String, List<MonthlyStatisticsDto>> getMonthlyStatisticsListByProject(String userName, String year) {
        String sdate = year + "-01-00";
        String edate = year + "-12-31";
        List<MonthlyStatisticsInterface> monthlyStatisticsInterfaces =  ticketRepository.findAllMonthlySumMdByProject(userName, sdate, edate);
        Map<String, List<MonthlyStatisticsDto>> resultMap = new HashMap<>();

        monthlyStatisticsInterfaces.forEach(i -> {
            resultMap.put(year+i.getMonth(), new ArrayList<MonthlyStatisticsDto>());
        });
        monthlyStatisticsInterfaces.forEach(i -> {
            MonthlyStatisticsDto dto = new MonthlyStatisticsDto(i);
            if(dto.getSum() != 0) {
                resultMap.get(year+i.getMonth()).add(dto);
            }
        });

        return resultMap;
    }

    public List<JobStatisticsDto> getJobStatisticsByDate(String userName, String year, String month) {
        List<JobStatisticsDto> result = new ArrayList<>();
        Specification<Ticket> spec = (con, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userName != null && !"".equals(userName)) {
                predicates.add(cb.equal(con.get("userName"), userName));
            }
            LocalDateTime startDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), 1, 0, 0);
            YearMonth lastDay = YearMonth.from(startDate);
            LocalDateTime endDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), lastDay.lengthOfMonth(), 0, 0);
            predicates.add(cb.between(con.get("startTime"), startDate, endDate));

            return predicates.size() > 0 ? cb.and(predicates.toArray(new Predicate[predicates.size()])) : null;
        };
        List<Ticket> list = ticketRepository.findAll(spec).stream().collect(Collectors.toList());
        list.forEach(i -> {
            result.add(new JobStatisticsDto(i));
        });

        return result;
    }
}
