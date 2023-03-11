package castis.domain.ticket.entity;

import castis.domain.project.entity.Project;
import castis.domain.ticket.dto.TicketDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Table(name = "ticket")
@Entity
@NoArgsConstructor
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

	@ManyToOne
	@JoinColumn(name = "projectname", insertable = false, updatable = false)
	private Project project;

	public Ticket(TicketDto dto) {
		if(dto.getId() != null && dto.getId() != 0) {
			this.no = dto.getId();
		}
		this.teamName = dto.getTeamName();
		this.userName = dto.getUserName();
		this.projectName = dto.getProject();
		this.title = dto.getTitle();
		this.subtitle = dto.getTitle();
		this.content = dto.getContent();

		double md = 0;
		if(dto.getMd() != null && !"".equals(dto.getMd())) {
			md = Float.parseFloat(dto.getMd());
			this.emd = (float) md;
			this.nmd = (float) md;
		}

		this.startTime = LocalDateTime.parse(dto.getEventStartDate() + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		this.endTime = LocalDateTime.parse(dto.getEventEndDate() + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

		this.attachedfile = "";
	}

}
