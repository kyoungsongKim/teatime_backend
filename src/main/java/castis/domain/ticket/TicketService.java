package castis.domain.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public List<EventDto> findAllByUserNameAndPeroid(String userName, String year, String month) {

        List<EventDto> result = new ArrayList<>();

        Specification<Ticket> spec = (con, query, cb) -> {
            /*// ordering
            List<Order> orders = new ArrayList<>();
            //orders.add(cb.asc(con.get("seq")));
            orders.add(cb.asc(con.get("order")));
            query.orderBy(orders);*/

            List<Predicate> predicates = new ArrayList<>();
            if(userName != null && !"".equals(userName)){
                predicates.add(cb.equal(con.get("userName"), userName));
            }
            LocalDateTime startDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), 1, 0, 0);
            YearMonth lastDay = YearMonth.from(startDate);
            LocalDateTime endDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), lastDay.lengthOfMonth(), 0, 0);
            predicates.add(cb.between(con.get("startTime"), startDate , endDate));

            return predicates.size() > 0 ? cb.and(predicates.toArray(new Predicate[predicates.size()])) : null;
        };
        List<Ticket> list = ticketRepository.findAll(spec).stream().collect(Collectors.toList());
        list.forEach(i -> {
            result.add(new EventDto(i));
        });

        return result;
    }
}
