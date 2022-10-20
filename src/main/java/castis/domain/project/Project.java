package castis.domain.project;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "project")
@Entity
@NoArgsConstructor
public class Project {

    @Id
    private String projectName;

    @Column
    private String site;

    @Column
    private String description;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column
    private String bgColor;

    @Builder
    public Project(String projectName, String site, String description, LocalDateTime startDate, LocalDateTime endDate, String bgColor) {
        this.projectName = projectName;
        this.site = site;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bgColor = bgColor;
    }
}
