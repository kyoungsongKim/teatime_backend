package castis.domain.project.controller;

import castis.domain.project.service.ProjectService;
import castis.domain.project.dto.SiteInterface;
import castis.domain.project.dto.ProjectDto;
import castis.domain.project.dto.SiteAndProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class ProjectController {

    private final ProjectService projectService;

    @RequestMapping(value = "/site", method = RequestMethod.GET)
    public ResponseEntity getSiteInfo(HttpServletRequest httpServletRequest) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<SiteInterface> siteDtoList = projectService.getSiteInfoList();
        return new ResponseEntity<>(siteDtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/project", method = RequestMethod.GET)
    public ResponseEntity getProjectInfo(HttpServletRequest httpServletRequest
            , @RequestParam(name = "siteName") String siteName) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<ProjectDto> projectDtoList = projectService.getProjectInfoListBySiteName(siteName);
        return new ResponseEntity<>(projectDtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/project/all", method = RequestMethod.GET)
    public ResponseEntity<SiteAndProjectDto> getAllProjectInfo(HttpServletRequest httpServletRequest) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<ProjectDto> projectList = projectService.getAllProjectInfoList();
        Set<String> siteList = new HashSet<>();
        Map<String, List<ProjectDto>> projectMap = new HashMap<String, List<ProjectDto>>();

        for (ProjectDto p : projectList) {
            siteList.add(p.getSite());
            if (!projectMap.containsKey(p.getSite())) {
                List<ProjectDto> projectNewList = new ArrayList<ProjectDto>();
                projectNewList.add(p);
                // new site
                projectMap.put(p.getSite(), projectNewList);
            } else {
                List<ProjectDto> createdProjectList = projectMap.get(p.getSite());
                createdProjectList.add(p);
            }
        }

        SiteAndProjectDto siteAndProjectDto = new SiteAndProjectDto();
        siteAndProjectDto.setSiteList(siteList);
        siteAndProjectDto.setProjectMap(projectMap);

        return new ResponseEntity<>(siteAndProjectDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/project", method = RequestMethod.POST)
    public ResponseEntity addProjectInfo(@RequestBody ProjectDto projectDto, HttpServletRequest httpServletRequest) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());

        List<ProjectDto> projectDtoList = projectService.getProjectInfoListBySiteName(projectDto.getSite());
        if (projectDtoList != null && projectDtoList.isEmpty() == false) {
            projectDto.setBgColor(projectDtoList.get(0).getBgColor());
        }
        projectDto.setDescription(projectDto.getProjectName());

        projectService.addProject(projectDto);

        return new ResponseEntity<>(projectDtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/project/{projectName}", method = RequestMethod.POST)
    public ResponseEntity updateEndDateProject(@PathVariable(value = "projectName") String projectName, HttpServletRequest httpServletRequest) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        projectService.updateEndDateProject(projectName);
        return new ResponseEntity<>(projectName + "end date update success", HttpStatus.OK);
    }
}
