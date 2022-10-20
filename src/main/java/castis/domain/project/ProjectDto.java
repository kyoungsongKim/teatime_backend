package castis.domain.project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link Project} entity
 */
@Setter
@Getter
@NoArgsConstructor
public class ProjectDto implements Serializable {
    private String projectName;
    private String site;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String bgColor;

    public ProjectDto(Project project) {
        this.projectName = project.getProjectName();
        this.site = project.getSite();
        this.description = project.getDescription();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.bgColor = project.getBgColor();
    }

    public Project toEntity() {
        Project build = Project.builder()
                .projectName(projectName)
                .site(site)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .bgColor(bgColor)
                .build();

        return build;
    }
}