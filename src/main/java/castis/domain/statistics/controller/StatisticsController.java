package castis.domain.statistics.controller;

import castis.domain.statistics.dto.JobStatisticsDto;
import castis.domain.statistics.dto.MonthlyStatisticsDto;
import castis.domain.statistics.service.StatisticsService;
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
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @RequestMapping(value = "/monthly", method = RequestMethod.GET)
    public ResponseEntity getMonthlyStatisticsByProject(
            HttpServletRequest httpServletRequest
            , @RequestParam(name = "userName") String userName
            , @RequestParam(name = "year") String year
    ){
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        Map<String, List<MonthlyStatisticsDto>> result = statisticsService.getMonthlyStatisticsListByProject(userName, year);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/job", method = RequestMethod.GET)
    public ResponseEntity getJobStatisticsByDate(
            HttpServletRequest httpServletRequest
            , @RequestParam(name = "userName") String userName
            , @RequestParam(name = "year") String year
            , @RequestParam(name = "month") String month
    ){
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<JobStatisticsDto> result = statisticsService.getJobStatisticsByDate(userName, year, month);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
