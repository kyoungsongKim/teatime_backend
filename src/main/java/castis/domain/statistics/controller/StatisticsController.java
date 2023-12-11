package castis.domain.statistics.controller;

import castis.domain.monthlysales.dto.MonthlySalesDto;
import castis.domain.monthlysales.service.MonthlySalesService;
import castis.domain.service.dto.ServiceChargeDTO;
import castis.domain.service.service.ServiceService;
import castis.domain.statistics.dto.JobStatisticsDto;
import castis.domain.statistics.dto.MonthlyStatisticsDto;
import castis.domain.statistics.service.StatisticsService;
import castis.domain.ticket.dto.EventDetailDto;
import castis.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final MonthlySalesService monthlySalesService;
    private final TicketService ticketService;
    private final ServiceService serviceService;

    @RequestMapping(value = "/monthly", method = RequestMethod.GET)
    public ResponseEntity getMonthlyStatisticsByProject(
            HttpServletRequest httpServletRequest, @RequestParam(name = "userName") String userName,
            @RequestParam(name = "year") String year) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        Map<String, List<MonthlyStatisticsDto>> result = statisticsService.getMonthlyStatisticsListByProject(userName,
                year);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/monthTicket", method = RequestMethod.GET)
    public ResponseEntity getMonthlyTickets(HttpServletRequest httpServletRequest,
            @RequestParam(name = "userName") String userName, @RequestParam(name = "year") String year,
            @RequestParam(name = "month") String month) throws IOException {
        log.info("request, uri[{}] periodYear[{}], periodMonth[{}], periodDay[{}], userName[{}]",
                httpServletRequest.getRequestURI(), year, month, userName);
        List<EventDetailDto> ticketDtoList = ticketService.findAllByUserNameAndMonth(userName, year, month);
        return new ResponseEntity<>(ticketDtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/job", method = RequestMethod.GET, params = { "userName", "year", "month" })
    public ResponseEntity getJobStatisticsByDate(
            HttpServletRequest httpServletRequest, @RequestParam(name = "userName") String userName,
            @RequestParam(name = "year") String year, @RequestParam(name = "month") String month) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<JobStatisticsDto> result = statisticsService.getJobStatisticsByDate(userName, year, month);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/job", method = RequestMethod.GET, params = { "userName", "year", "month", "date" })
    public ResponseEntity getJobStatisticsByDate(
            HttpServletRequest httpServletRequest, @RequestParam(name = "userName") String userName,
            @RequestParam(name = "year") String year, @RequestParam(name = "month") String month,
            @RequestParam(name = "date") String date) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<JobStatisticsDto> result = statisticsService.getJobStatisticsByDate(userName, year, month, date);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/sales", method = RequestMethod.GET)
    public ResponseEntity getMonthlySalesList(
            HttpServletRequest httpServletRequest, @RequestParam(name = "userName") String userName,
            @RequestParam(name = "year") int year) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<MonthlySalesDto> monthlySales = monthlySalesService.findAllByUsernameAndYear(userName, year);
        List<Integer> yearList = monthlySalesService.getYearListHasMonthlySales(userName);
        ServiceChargeDTO serviceCharge = serviceService.getServiceChargeByUserIdAndServiceId(userName, 5);
        HashMap<String, Object> result = new HashMap<>();
        result.put("salesList", monthlySales);
        result.put("yearList", yearList);
        result.put("targetSales", serviceCharge != null ? serviceCharge.getCharge() : null);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
