package castis.domain.point.controller;

import castis.domain.point.dto.PointAndLevelDto;
import castis.domain.point.dto.PointHistoryDto;
import castis.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/point")
public class PointController {

    private final PointService pointService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity getPointListDataByReceiver(
            HttpServletRequest httpServletRequest
            , @RequestParam(name = "receiver") String receiver
            , @RequestParam(name = "periodYear", required = false) String periodYear
    ) {
        log.info("request, uri[{}], receiver[{}] periodYear[{}]", httpServletRequest.getRequestURI(), receiver, periodYear);
        List<PointHistoryDto> pointHistoryDtoList = pointService.findAllPointHistoryByRecver(receiver, periodYear);

        if (pointHistoryDtoList != null) {
            return new ResponseEntity<>(pointHistoryDtoList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/pointCode", method = RequestMethod.GET)
    public ResponseEntity getPointCodeData(
            HttpServletRequest httpServletRequest
            , @RequestParam(name = "sender") String sender
            , @RequestParam(name = "receiver") String receiver
            , @RequestParam(name = "memo") String memo
            , @RequestParam(name = "point") Integer point
    ) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        String code = pointService.savePointHistoryAndCodeReturn(sender, receiver, point, memo);

        if (code != null) {
            return new ResponseEntity<>(code, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/pointCode", method = RequestMethod.POST)
    public ResponseEntity getPointCodeData(
            HttpServletRequest httpServletRequest
            , @RequestBody PointHistoryDto pointHistoryDto
    ) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        return pointService.updatePointHistoryComplete(pointHistoryDto.getCode());
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<PointAndLevelDto> getUserPoint(
            HttpServletRequest httpServletRequest
            , @RequestParam(name = "receiver") String receiver
    ) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());

        List<PointHistoryDto> pointHistoryList = pointService.findAllPointHistoryByRecver(receiver, null);
        int totalPoint = 0;
        for (PointHistoryDto pHistory : pointHistoryList) {
            if (pHistory.getUseDate() != null) {
                //add only used code
                totalPoint += pHistory.getPoint();
            }
        }
        int userLevel = (int) Math.sqrt((int) (totalPoint / 1000));

        PointAndLevelDto pointAndLevelDto = new PointAndLevelDto(totalPoint, userLevel);

        return new ResponseEntity<>(pointAndLevelDto, HttpStatus.OK);
    }
}