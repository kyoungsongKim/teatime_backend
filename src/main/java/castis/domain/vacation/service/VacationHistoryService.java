package castis.domain.vacation.service;

import castis.domain.vacation.entity.VacationHistory;
import castis.domain.vacation.repository.VacationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacationHistoryService {

    private final VacationHistoryRepository vacationHistoryRepository;

    public ResponseEntity saveVacationHistory(VacationHistory vacationHistory) {
        vacationHistoryRepository.save(vacationHistory);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    public ResponseEntity deleteVacationHistory(Long no) {
        vacationHistoryRepository.deleteByTicketNo(no, VacationHistory.STATUS_READY);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    public List<VacationHistory> getVacationHistoryBySendDateToday() {
        LocalDate currentDate = LocalDate.now();

        Specification<VacationHistory> spec = (con, query, cb) -> {
            // ordering
            List<Order> orders = new ArrayList<>();
            //orders.add(cb.asc(con.get("seq")));
            orders.add(cb.desc(con.get("createDate")));
            query.orderBy(orders);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.between(con.get("sendDate"), currentDate, currentDate));

            return predicates.size() > 0 ? cb.and(predicates.toArray(new Predicate[predicates.size()])) : null;
        };
        List<VacationHistory> list = vacationHistoryRepository.findAll(spec).stream().collect(Collectors.toList());

        return list;
    }

    public void update(VacationHistory vh) {
        vacationHistoryRepository.save(vh);
    }
}
