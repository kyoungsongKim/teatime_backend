package castis.domain.statistics.dto;

import castis.domain.ticket.entity.Ticket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Setter
@Getter
@NoArgsConstructor
public class JobStatisticsDto {

    private Long id;
    private String startDate;
    private String endDate;
    private String projectName;
    private String title;
    private String content;
    private Long nmd;

    public JobStatisticsDto(Ticket ticket) {
        this.id = ticket.getNo();
        this.startDate = ticket.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.endDate = ticket.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.projectName = ticket.getProjectName();
        this.title = ticket.getProjectName();
        this.content = ticket.getContent();
        this.nmd = ticket.getNmd().longValue();
    }
}
