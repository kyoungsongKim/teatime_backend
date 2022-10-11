package castis.domain.ticket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link Ticket} entity
 */
@Setter
@Getter
@NoArgsConstructor
public class EventDetailDto implements Serializable {
    private String teamName;
    private String userName;
    private String projectName;
    private String title;
    private String content;
    private Float emd;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String site;
    private String bgColor;

    public EventDetailDto(Ticket ticket) {
        this.teamName = ticket.getTeamName();
        this.userName = ticket.getUserName();
        this.projectName = ticket.getProjectName();
        this.title = ticket.getTitle();
        this.content = ticket.getContent();
        this.content = ticket.getContent();
        this.emd = ticket.getEmd();
        this.startTime = ticket.getStartTime();
        this.endTime = ticket.getEndTime();
    }
}