package castis.domain.report.service;

import castis.domain.report.entity.ReportHistory;
import castis.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public void saveReport(String userId, String recvEmail, String title, String contents, String isSuccess) {
        ReportHistory report = new ReportHistory(userId, recvEmail, title, contents, isSuccess);
        reportRepository.save(report);
    }

}
