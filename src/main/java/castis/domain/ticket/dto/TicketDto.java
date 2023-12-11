package castis.domain.ticket.dto;

import castis.domain.project.entity.Project;
import castis.domain.ticket.entity.Ticket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class TicketDto {
    private Long id;
    private String teamName;
    private String userName;
    private String title;
    private String site;
    private String project;
    private String md;
    private String eventStartDate;
    private String eventEndDate;
    private String content;

    public TicketDto(Ticket ticket, Project project) {
        this.id = ticket.getNo();
        this.teamName = ticket.getTeamName();
        this.userName = ticket.getUserName();
        this.title = ticket.getProjectName();
        this.site = project.getSite();
        this.project = project.getProjectName();
        this.md = String.valueOf(ticket.getEmd());
        this.eventStartDate = ticket.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.eventEndDate = ticket.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.content = ticket.getContent();
    }
}
