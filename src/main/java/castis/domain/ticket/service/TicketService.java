package castis.domain.ticket.service;

import castis.domain.point.entity.PointHistory;
import castis.domain.point.repository.PointHistoryRepository;
import castis.domain.project.entity.Project;
import castis.domain.project.repository.ProjectRepository;
import castis.domain.ticket.dto.EventDetailDto;
import castis.domain.ticket.dto.EventDto;
import castis.domain.ticket.dto.TicketDto;
import castis.domain.ticket.entity.Ticket;
import castis.domain.ticket.repository.TicketRepository;
import castis.domain.vacation.entity.VacationHistory;
import castis.domain.vacation.service.VacationHistoryService;
import castis.util.holiday.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final HolidayService holidayService;
    private final VacationHistoryService vacationHistoryService;

    public List<EventDto> findAllByUserNameAndPeroid(String userName, String year, String month) throws IOException {

        List<EventDto> result = new ArrayList<>();

        Specification<Ticket> spec = (con, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userName != null && !"".equals(userName)) {
                predicates.add(cb.equal(con.get("userName"), userName));
            }
            LocalDateTime startDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), 1, 0, 0);
            YearMonth lastDay = YearMonth.from(startDate);
            LocalDateTime endDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month),
                    lastDay.lengthOfMonth(), 0, 0);
            predicates.add(cb.between(con.get("startTime"), startDate, endDate));

            return predicates.size() > 0 ? cb.and(predicates.toArray(new Predicate[predicates.size()])) : null;
        };
        long startTime = System.nanoTime();
        List<Ticket> list = ticketRepository.findAll(spec).stream().collect(Collectors.toList());
        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000;

        list.forEach(i -> {
            result.add(new EventDto(i));
        });

        long startTime1 = System.nanoTime();
        holidayService.getHolidayInfo(year, month).forEach(h -> {
            result.add(new EventDto(h));
        });
        long endTime1 = System.nanoTime();

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
                LocalDateTime startDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month),
                        Integer.parseInt(day), 0, 0);
                LocalDateTime endDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month),
                        Integer.parseInt(day), 23, 59, 59);
                predicates.add(cb.between(con.get("startTime"), startDate, endDate));
            } else {
                LocalDateTime startDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), 1, 0, 0);
                YearMonth lastDay = YearMonth.from(startDate);
                LocalDateTime endDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month),
                        lastDay.lengthOfMonth(), 0, 0);
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

    public List<EventDetailDto> findAllByUserNameAndMonth(String userName, String year, String month) {

        List<EventDetailDto> result = new ArrayList<>();

        Specification<Ticket> spec = (con, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userName != null && !"".equals(userName)) {
                predicates.add(cb.equal(con.get("userName"), userName));
            }
            LocalDateTime startDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), 1, 0, 0);
            YearMonth lastDay = YearMonth.from(startDate);
            LocalDateTime endDate = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month),
                    lastDay.lengthOfMonth(), 0, 0);
            predicates.add(cb.between(con.get("startTime"), startDate, endDate));

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
        Ticket ticket = ticketRepository.save(new Ticket(ticketDto));
        LocalDateTime date = LocalDateTime.parse(ticketDto.getEventStartDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime createDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0);
        if (ticketDto.getId() == null || ticketDto.getId() == 0) {
            List<PointHistory> pointHistoryList = pointHistoryRepository
                    .findAllByRecverAndCreateDateAndCode(ticketDto.getUserName(), createDate, "AUTO").orElse(null);
            ;
            if (pointHistoryList == null || pointHistoryList.size() == 0) {
                PointHistory pointHistory = new PointHistory(ticketDto.getUserName(), ticketDto.getUserName(), 500, 0,
                        "TICKET_POINT", "AUTO");
                pointHistory.setUseDate(LocalDateTime.now());
                pointHistoryRepository.save(pointHistory);
            }
        }
        // if (ticketDto.getProject().equals("휴가")) {
        // vacationHistoryService.saveVacationHistory(new VacationHistory(ticket));
        // }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    public TicketDto getTicketInfoByNo(Long no) {
        Ticket ticket = ticketRepository.findById(no).orElse(null);
        Project project = projectRepository.findById(ticket.getProjectName()).orElse(null);
        TicketDto dto = new TicketDto(ticket, project);
        return dto;
    }

    @Transactional
    public ResponseEntity updateTicketInfoByNo(Long no, TicketDto ticketDto) {
        Ticket ticket = ticketRepository.findById(no).orElse(null);
        Ticket newTicket = new Ticket(ticketDto);
        if (ticket == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // newTicket과 ticket의 필드를 비교하여 변경된 필드만 수정
        if (newTicket.getTeamName() != null && !newTicket.getTeamName().equals(ticket.getTeamName())) {
            ticket.setTeamName(newTicket.getTeamName());
        }
        if (newTicket.getUserName() != null && !newTicket.getUserName().equals(ticket.getUserName())) {
            ticket.setUserName(newTicket.getUserName());
        }
        if (newTicket.getProjectName() != null && !newTicket.getProjectName().equals(ticket.getProjectName())) {
            ticket.setProjectName(newTicket.getProjectName());
        }
        if (newTicket.getTitle() != null && !newTicket.getTitle().equals(ticket.getTitle())) {
            ticket.setTitle(newTicket.getTitle());
        }
        if (newTicket.getSubtitle() != null && !newTicket.getSubtitle().equals(ticket.getSubtitle())) {
            ticket.setSubtitle(newTicket.getSubtitle());
        }
        if (newTicket.getContent() != null && !newTicket.getContent().equals(ticket.getContent())) {
            ticket.setContent(newTicket.getContent());
        }
        if (newTicket.getEmd() != null && !newTicket.getEmd().equals(ticket.getEmd())) {
            ticket.setEmd(newTicket.getEmd());
        }
        if (newTicket.getNmd() != null && !newTicket.getNmd().equals(ticket.getNmd())) {
            ticket.setNmd(newTicket.getNmd());
        }
        if (newTicket.getStartTime() != null && !newTicket.getStartTime().equals(ticket.getStartTime())) {
            ticket.setStartTime(newTicket.getStartTime());
        }
        if (newTicket.getEndTime() != null && !newTicket.getEndTime().equals(ticket.getEndTime())) {
            ticket.setEndTime(newTicket.getEndTime());
        }
        if (newTicket.getHistory() != null && !newTicket.getHistory().equals(ticket.getHistory())) {
            ticket.setHistory(newTicket.getHistory());
        }

        ticketRepository.save(ticket);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity deleteTicketInfoByNo(Long no) {
        ticketRepository.deleteById(no);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
