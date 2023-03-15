package castis.domain.ticket.dto;

import castis.domain.ticket.entity.Ticket;
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
    private Long emd;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String site;
    private String bgColor;

    public EventDetailDto(Ticket ticket) {
        this.teamName = ticket.getTeamName();
        this.userName = ticket.getUserName();
        this.projectName = ticket.getProjectName();
        this.title = ticket.getProjectName();
        this.content = ticket.getContent();
        this.content = ticket.getContent();
        this.emd = ticket.getEmd().longValue();
        this.startTime = ticket.getStartTime();
        this.endTime = ticket.getEndTime();
    }
}