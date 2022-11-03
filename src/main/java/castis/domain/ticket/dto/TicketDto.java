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
        this.title = ticket.getTitle();
        this.site = project.getSite();
        this.project = project.getProjectName();

        String hour = "00";
        String minute = "00";
        double md = ticket.getEmd().doubleValue() * 8;
        int h = (int) md;
        double m = (double) (md - (int) md) * 60;
        if((int)(Math.log10(m)+1) < 2) {
            hour = "0" + String.valueOf(h);
        }else{
            hour = String.valueOf(h);
        }
        if((int)(Math.log10(m)+1) < 2) {
            minute = "0" + String.valueOf((int)m);
        } else {
            minute = String.valueOf((int)m);
        }


        this.md = hour + ":" + minute;
        this.eventStartDate = ticket.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.eventEndDate = ticket.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.content = ticket.getContent();
    }
}
