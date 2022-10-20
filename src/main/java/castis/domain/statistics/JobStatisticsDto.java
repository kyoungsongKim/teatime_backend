package castis.domain.statistics;

import castis.domain.ticket.Ticket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
    private Float nmd;

    public JobStatisticsDto(Ticket ticket) {
        this.id = ticket.getNo();
        this.startDate = ticket.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.endDate = ticket.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.projectName = ticket.getProjectName();
        this.title = ticket.getTitle();
        this.content = ticket.getContent();
        this.nmd = ticket.getNmd();
    }
}
