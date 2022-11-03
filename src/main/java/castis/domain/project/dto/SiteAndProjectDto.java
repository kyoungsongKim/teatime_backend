package castis.domain.project.dto;

import castis.domain.project.entity.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A DTO for the {@link Project} entity
 */
@Setter
@Getter
@NoArgsConstructor
public class SiteAndProjectDto {
    private Set<String> siteList;
    private Map<String, List<ProjectDto>> projectMap;
}