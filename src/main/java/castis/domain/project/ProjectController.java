package castis.domain.project;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class ProjectController {

    private final ProjectService projectService;

    @RequestMapping(value = "/site", method = RequestMethod.GET)
    public ResponseEntity getSiteInfo(HttpServletRequest httpServletRequest){
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<SiteInterface> siteDtoList = projectService.getSiteInfoList();
        return new ResponseEntity<>(siteDtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/project", method = RequestMethod.GET)
    public ResponseEntity getProjectInfo(HttpServletRequest httpServletRequest
            , @RequestParam(name = "siteName") String siteName){
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<ProjectDto> projectDtoList = projectService.getProjectInfoListBySiteName(siteName);
        return new ResponseEntity<>(projectDtoList, HttpStatus.OK);
    }
}
