package castis.domain.project;

import castis.domain.project.dto.ProjectDto;
import castis.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<SiteInterface> getSiteInfoList() {
        return projectRepository.findAllSiteInfo();
    }

    public List<ProjectDto> getProjectInfoListBySiteName(String siteName) {
        List<Project> projectList = projectRepository.findAllBySite(siteName).orElse(null);

        List<ProjectDto> projectDtoList = new ArrayList<>();
        projectList.forEach(i -> {
            projectDtoList.add(new ProjectDto(i));
        });

        return projectDtoList;
    }

    public List<ProjectDto> getAllProjectInfoList() {
        List<Project> projectList = projectRepository.findAll();

        List<ProjectDto> projectDtoList = new ArrayList<>();
        projectList.forEach(i -> {
            projectDtoList.add(new ProjectDto(i));
        });

        return projectDtoList;
    }

    public boolean addProject(ProjectDto projectDto) {
        projectRepository.save(projectDto.toEntity());
        return true;
    }

    public boolean deleteProject(String projectName) {
        projectRepository.deleteById(projectName);
        return true;
    }

    public boolean updateEndDateProject(String projectName) {
        Project project = projectRepository.findById(projectName).orElseThrow(() -> new NotFoundException("Project Not Found"));
        project.setEndDate(LocalDateTime.now().minusDays(1));
        projectRepository.save(project);
        return true;
    }
}
