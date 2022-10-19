package castis.domain.ticket;

import castis.domain.point.PointHistory;
import castis.domain.point.PointHistoryRepository;
import castis.domain.project.Project;
import castis.domain.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final ProjectRepository projectRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public List<EventDto> findAllByUserNameAndPeroid(String userName, String year, String month) {

        List<EventDto> result = new ArrayList<>();

        Specification<Ticket> spec = (con, query, cb) -> {
            /*// ordering
            List<Order> orders = new ArrayList<>();
            //orders.add(cb.asc(con.get("seq")));
            orders.add(cb.asc(con.get("order")));
            query.orderBy(orders);*/

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
            result.add(new EventDto(i));
        });

        return result;
    }

    public List<EventDetailDto> findAllByUserNameAndPeroid(String userName, String year, String month, String day) {

        List<EventDetailDto> result = new ArrayList<>();

        Specification<Ticket> spec = (con, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userName != null && !"".equals(userName)) {
                predicates.add(cb.equal(con.get("userName"), userName));
            }

            if (day != null && !"".equals(day)) {
                LocalDateTime startDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), 0, 0);
                LocalDateTime endDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), 23, 59, 59);
                predicates.add(cb.between(con.get("startTime"), startDate, endDate));
            } else {
                LocalDateTime startDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), 1, 0, 0);
                YearMonth lastDay = YearMonth.from(startDate);
                LocalDateTime endDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), lastDay.lengthOfMonth(), 0, 0);
                predicates.add(cb.between(con.get("startTime"), startDate, endDate));
            }

            return predicates.size() > 0 ? cb.and(predicates.toArray(new Predicate[predicates.size()])) : null;
        };
        List<Project> projectList = projectRepository.findAll();
        List<Ticket> list = ticketRepository.findAll(spec).stream().collect(Collectors.toList());
        list.forEach(i -> {
            EventDetailDto eventDetailDto = new EventDetailDto(i);
            for (Project project : projectList) {
                if (project.getProjectName().equalsIgnoreCase(eventDetailDto.getProjectName())) {
                    eventDetailDto.setSite(project.getSite());
                    eventDetailDto.setBgColor(project.getBgColor());
                    break;
                }
            }
            result.add(eventDetailDto);
        });

        return result;
    }

    public ResponseEntity saveTicketInfo(TicketDto ticketDto) {
        ticketRepository.save(new Ticket(ticketDto));
        PointHistory pointHistory = new PointHistory(ticketDto.getUserName(), ticketDto.getUserName(), 5, "TICKET_POINT", "AUTO");
        pointHistory.setUseDate(LocalDateTime.now());
        pointHistoryRepository.save(pointHistory);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    public TicketDto getTicketInfoByNo(Long no) {
        Ticket ticket = ticketRepository.findById(no).orElse(null);
        Project project = projectRepository.findById(ticket.getProjectName()).orElse(null);
        TicketDto dto = new TicketDto(ticket, project);
        return dto;
    }
}
