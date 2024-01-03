package castis.domain.summary.dto;

import castis.domain.summary.entity.Summary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class SummaryDto {
    private String userId;
    private Integer year;
    private Integer month;
    private String text;
    private LocalDateTime createDate;

    public Summary toEntity() {
        Summary build = Summary.builder()
                .userId(userId)
                .year(year)
                .month(month)
                .text(text)
                .createDate(LocalDateTime.now())
                .build();
        return build;
    }
}
