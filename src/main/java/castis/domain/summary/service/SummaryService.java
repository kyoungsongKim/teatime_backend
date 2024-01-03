package castis.domain.summary.service;

import castis.domain.summary.dto.SummaryDto;
import castis.domain.summary.entity.Summary;
import castis.domain.summary.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;

    public Summary findBySummaryIdAndYearAndMonth(String userId, Integer year, Integer month) {
        Optional<Summary> summary = summaryRepository.findByUserIdAndYearAndMonth(userId, year, month);
        if (summary.isPresent()) {
            return summary.get();
        }
        return null;
    }

    @Transactional
    public void addSummary(SummaryDto summaryDto) {
        Optional<Summary> summary = summaryRepository.findByUserIdAndYearAndMonth(summaryDto.getUserId(), summaryDto.getYear(), summaryDto.getMonth());
        if (summary.isPresent()) {
            summary.get().setText(summaryDto.getText());
            summaryRepository.save(summary.get());
        } else {
            summaryRepository.save(summaryDto.toEntity());
        }
    }
}
