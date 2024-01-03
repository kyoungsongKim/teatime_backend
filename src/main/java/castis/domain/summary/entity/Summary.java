package castis.domain.summary.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@Table(name = "summary")
@Entity
@NoArgsConstructor
public class Summary {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String userId;

	@Column
	private Integer year;

	@Column
	private Integer month;

	@Column
	private String text;

	@Column
	private LocalDateTime createDate;

	@Builder
	public Summary(Long id, String userId, Integer year, Integer month, String text, LocalDateTime createDate) {
		this.id = id;
		this.userId = userId;
		this.year = year;
		this.month = month;
		this.text = text;
		this.createDate = createDate;
	}
}
