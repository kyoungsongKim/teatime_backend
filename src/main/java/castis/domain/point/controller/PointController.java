package castis.domain.point.controller;

import castis.domain.point.dto.PointAndLevelDto;
import castis.domain.point.dto.PointHistoryDto;
import castis.domain.point.dto.PointSummaryDto;
import castis.domain.point.service.PointService;
import castis.domain.user.entity.User;
import castis.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/point")
public class PointController {

    private final PointService pointService;
    private final UserRepository userRepository;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity getPointListDataByReceiver(
            HttpServletRequest httpServletRequest
            , @RequestParam(name = "receiver") String receiver
            , @RequestParam(name = "periodYear", required = false) String periodYear
    ) {
        log.info("request, uri[{}], receiver[{}] periodYear[{}]", httpServletRequest.getRequestURI(), receiver, periodYear);
        List<PointHistoryDto> pointHistoryDtoList = pointService.findAllPointHistoryByRecver(receiver, periodYear);
        if (pointHistoryDtoList != null) {
            Iterator<PointHistoryDto> phIterrator = pointHistoryDtoList.iterator();
            while(phIterrator.hasNext()){
                PointHistoryDto curDto = phIterrator.next();
                if(curDto!=null){
                    if (curDto.getUseDate() == null ) {
                        phIterrator.remove();
                    } else if (curDto.getCode().contains("OLD")) {
                        phIterrator.remove();
                    } else if (curDto.getCode().contains("_COMPLETE")) {
                        curDto.setCode(curDto.getCode().substring(0,4));
                    }
                }
            }
            return new ResponseEntity<>(pointHistoryDtoList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public ResponseEntity getPointListDataByReceiver(HttpServletRequest httpServletRequest) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<PointSummaryDto> pointSummaryDtoList = new ArrayList<>();
        List<PointHistoryDto> pointHistoryDtoList = pointService.findAllPointHistory();
        List<User> userList = userRepository.findAll();
        if (pointHistoryDtoList != null) {
            for (User curUser : userList) {
                PointSummaryDto curSummary = new PointSummaryDto();
                curSummary.setUserId(curUser.getId());
                curSummary.setRealName(curUser.getRealName());
                int totalPoint = 0;
                int totalExp = 0;
                Iterator<PointHistoryDto> phIterrator = pointHistoryDtoList.iterator();
                while(phIterrator.hasNext()){
                    PointHistoryDto curDto = phIterrator.next();
                    if(curDto!=null){
                        if (curDto.getRecver().equalsIgnoreCase(curUser.getId()) && curDto.getUseDate() != null ) {
                            totalPoint += curDto.getPoint();
                            totalExp += curDto.getExpValue();
                        }
                    }
                }
                curSummary.setTotalPoint(totalPoint);
                curSummary.setTotalExp(totalExp);
                int level = (int) Math.sqrt(totalExp / 10000);
                curSummary.setLevel(level);
                pointSummaryDtoList.add(curSummary);
            }
            return new ResponseEntity<>(pointSummaryDtoList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity updatePointCodeData(
            HttpServletRequest httpServletRequest
            , @RequestBody PointHistoryDto pointHistoryDto
    ) throws Exception {
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
        int totalExp = 0;
        for (PointHistoryDto pHistory : pointHistoryList) {
            if (pHistory.getUseDate() != null) {
                //add only used code
                totalPoint += pHistory.getPoint();
            }
            totalExp += pHistory.getExpValue();
        }
        int userLevel = (int) Math.sqrt((int) (totalExp / 10000));

        PointAndLevelDto pointAndLevelDto = new PointAndLevelDto(totalPoint, userLevel, totalExp);

        return new ResponseEntity<>(pointAndLevelDto, HttpStatus.OK);
    }
}