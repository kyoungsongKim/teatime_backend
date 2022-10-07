package castis.domain.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<SiteInterface> getSiteInfoList() {
        return projectRepository.findAllSiteInfo();
    }
}
