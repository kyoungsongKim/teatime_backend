package castis.domain.project;

import castis.domain.ticket.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
