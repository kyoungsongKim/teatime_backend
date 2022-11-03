package castis.domain.project.dto;

import castis.domain.project.entity.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    private String startDate;

    private String endDate;
    private String bgColor;

    public ProjectDto(Project project) {
        this.projectName = project.getProjectName();
        this.site = project.getSite();
        this.description = project.getDescription();
        this.startDate = project.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.endDate = project.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.bgColor = project.getBgColor();
    }

    public Project toEntity() {
        Project build = Project.builder()
                .projectName(projectName)
                .site(site)
                .description(description)
                .startDate(LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE).atTime(0, 0, 0))
                .endDate(LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE).atTime(0, 0, 0))
                .bgColor(bgColor)
                .build();

        return build;
    }
}