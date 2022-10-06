package castis.domain.ticket;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

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

	
}
