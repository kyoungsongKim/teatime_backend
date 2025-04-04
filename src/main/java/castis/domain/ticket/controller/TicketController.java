package castis.domain.ticket.controller;

import castis.domain.ticket.dto.EventDetailDto;
import castis.domain.ticket.dto.EventDto;
import castis.domain.ticket.dto.TicketDto;
import castis.domain.ticket.service.TicketService;
import castis.domain.user.service.UserService;
import castis.domain.user.entity.User;
import castis.util.holiday.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    private final HolidayService holidayService;

    private final UserService userService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity getTicketDataUsingUserNameAndPeroid(
            HttpServletRequest httpServletRequest, @RequestParam(name = "periodYear") String periodYear,
            @RequestParam(name = "periodMonth") String periodMonth,
            @RequestParam(name = "userName", required = false) String userName) throws IOException {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<EventDto> ticketDtoList = ticketService.findAllByUserNameAndPeriod(userName, periodYear, periodMonth);

        return new ResponseEntity<>(ticketDtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity getTicketData(
            HttpServletRequest httpServletRequest, @RequestParam(name = "periodYear") String periodYear,
            @RequestParam(name = "periodMonth") String periodMonth,
            @RequestParam(name = "periodDay", required = false) String periodDay,
            @RequestParam(name = "userName", required = false) String userName) {
        log.info("request, uri[{}] periodYear[{}], periodMonth[{}], periodDay[{}], userName[{}]",
                httpServletRequest.getRequestURI(), periodYear, periodMonth, periodDay, userName);
        List<EventDetailDto> ticketDtoList = ticketService.findAllByUserNameAndPeriod(userName, periodYear, periodMonth,
                periodDay);

        return new ResponseEntity<>(ticketDtoList, HttpStatus.OK);
    }

    @PostMapping(value = "/")
    public ResponseEntity saveTicketData(HttpServletRequest httpServletRequest,
            @RequestBody TicketDto ticketDto) {
        log.info("request, uri[{}] ticketDto[{}]", httpServletRequest.getRequestURI(), ticketDto.toString());
        User userInfo = userService.getUser(ticketDto.getUserName());

        if (userInfo == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        ticketDto.setTeamName(userInfo.getTeamName());
        return ticketService.saveTicketInfo(ticketDto);
    }

    @RequestMapping(value = "/{no}", method = RequestMethod.GET)
    public ResponseEntity getTicketDataByNo(HttpServletRequest httpServletRequest,
            @PathVariable(value = "no") Long no) {
        log.info("request, uri[{}] No[{}]", httpServletRequest.getRequestURI(), no);
        TicketDto dto = ticketService.getTicketInfoByNo(no);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{no}", method = RequestMethod.PUT)
    public ResponseEntity updateTicketData(HttpServletRequest httpServletRequest, @PathVariable(value = "no") Long no,
            @RequestBody TicketDto ticketDto) {
        log.info("request, uri[{}] No[{}], ticketDto[{}]", httpServletRequest.getRequestURI(), no, ticketDto.toString());
        User userInfo = userService.getUser(ticketDto.getUserName());

        if (userInfo == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        ticketDto.setTeamName(userInfo.getTeamName());
        return ticketService.updateTicketInfoByNo(no, ticketDto);
    }

    @DeleteMapping(value = "/{no}")
    public ResponseEntity deleteTicketData(HttpServletRequest httpServletRequest, @PathVariable(value = "no") Long no) {
        log.info("request, uri[{}] No[{}]", httpServletRequest.getRequestURI(), no);
        ticketService.deleteTicketInfoByNo(no);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
