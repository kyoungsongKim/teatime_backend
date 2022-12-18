package castis.domain.expValue.controller;

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
@RequestMapping(value = "/api/exp")
public class ExpController {

    private final PointService pointService;
    private final UserRepository userRepository;

    @RequestMapping(value = "/levelup", method = RequestMethod.GET)
    public ResponseEntity getLevelUp(
            HttpServletRequest httpServletRequest
            , @RequestParam(name = "sender") String sender
            , @RequestParam(name = "receiver") String receiver
            , @RequestParam(name = "memo") String memo
            , @RequestParam(name = "point") Integer point
    ) {
        log.info("request, uri[{}] receiver[{}] memo[{}] point[{}]", httpServletRequest.getRequestURI(), receiver, memo, point);
        pointService.changePointToExp(sender, receiver, point, memo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/levelupall", method = RequestMethod.GET)
    public ResponseEntity getLevelUp(
            HttpServletRequest httpServletRequest
            , @RequestParam(name = "sender") String sender
            , @RequestParam(name = "memo") String memo
            , @RequestParam(name = "point") Integer point
    ) {
        log.info("request, uri[{}] memo[{}] point[{}]", httpServletRequest.getRequestURI(), memo, point);
        List<User> userList = userRepository.findAll();
        userList.forEach(user -> {
            String userId = user.getId();
            pointService.changePointToExp(sender, userId, point, memo);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}