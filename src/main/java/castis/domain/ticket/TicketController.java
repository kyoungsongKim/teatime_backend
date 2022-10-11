package castis.domain.ticket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/ticket")
public class TicketController {

    String calendarTimeFormat = "yyyy-MM-dd";
    DateFormat format = new SimpleDateFormat(calendarTimeFormat);

    private final TicketService ticketService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity getTicketDataUsingUserNameAndPeroid(
            HttpServletRequest httpServletRequest
            , @RequestParam(name = "periodYear") String periodYear
            , @RequestParam(name = "periodMonth") String periodMonth
            , @RequestParam(name = "userName", required = false) String userName
    ){
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<EventDto> ticketDtoList = ticketService.findAllByUserNameAndPeroid(userName, periodYear, periodMonth);

        return new ResponseEntity<>(ticketDtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity getTicketData(
            HttpServletRequest httpServletRequest
            , @RequestParam(name = "periodYear") String periodYear
            , @RequestParam(name = "periodMonth") String periodMonth
            , @RequestParam(name = "periodDay", required = false) String periodDay
            , @RequestParam(name = "userName", required = false) String userName
    ) {
        log.info("request, uri[{}] periodYear[{}], periodMonth[{}], periodDay[{}], userName[{}]", httpServletRequest.getRequestURI(), periodYear, periodMonth, periodDay, userName);
        List<EventDetailDto> ticketDtoList = ticketService.findAllByUserNameAndPeroid(userName, periodYear, periodMonth, periodDay);

        return new ResponseEntity<>(ticketDtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity saveTicketData(HttpServletRequest httpServletRequest, @RequestBody TicketDto ticketDto) {
        log.info("request, uri[{}] ticketDto[{}]", httpServletRequest.getRequestURI(), ticketDto.toString());
        return ticketService.saveTicketInfo(ticketDto);
    }

}
