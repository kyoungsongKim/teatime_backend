package castis.domain.vacation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

}
