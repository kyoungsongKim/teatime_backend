package castis.scheduler.sms;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SMSHistoryService {

    private final SMSHistoryRepository smsHistoryRepository;

    public List<SMSHistory> findAllSMSHistory(){
        List<SMSHistory> list = smsHistoryRepository.findAll();
        return list;
    }

    public void saveSMSHistory(String fromWho, String toWho, String body, String status) {
        SMSHistory smsHistory = new SMSHistory(fromWho, toWho, body, status, LocalDateTime.now());
        smsHistoryRepository.save(smsHistory);
    }
}
