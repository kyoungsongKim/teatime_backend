package castis.domain.ticket;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.parameters.P;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@Setter
@Table(name = "ticket")
@Entity
public class Ticket {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no;

	@Column
	private String teamName;

	@Column
	private String userName;

	@Column
	private String projectName;

	@Column
	private String title;

	@Column
	private String subtitle;

	@Column
	private String content;

	@Column
	private Float nmd;

	@Column
	private Float emd;

	@Column
	private String history;

	@Column
	private String attachedfile;

	@Column
	private LocalDateTime startTime;

	@Column
	private LocalDateTime endTime;

	public Ticket(TicketDto dto) {
		this.teamName = dto.getTeamName();
		this.userName = dto.getUserName();
		this.projectName = dto.getProject();
		this.title = dto.getTitle();
		this.subtitle = dto.getTitle();
		this.content = dto.getContent();

		this.emd = Float.parseFloat(dto.getMd());
		this.nmd = Float.parseFloat(dto.getMd());

		if(dto.isAllDay()) {
			this.startTime = LocalDateTime.parse(dto.getEventStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			this.endTime = LocalDateTime.parse(dto.getEventEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} else {
			this.startTime = LocalDateTime.parse(dto.getEventStartDate() + " " + dto.getEventStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
			this.endTime = LocalDateTime.parse(dto.getEventEndDate() + " " + dto.getEventEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		}
	}
	
}
