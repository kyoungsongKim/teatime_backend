package castis.domain.summary.controller;

import castis.domain.summary.dto.SummaryDto;
import castis.domain.summary.entity.Summary;
import castis.domain.summary.service.SummaryService;
import castis.domain.user.entity.User;
import castis.domain.user.service.UserService;
import castis.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class SummaryController {

    private final UserService userService;

    private final SummaryService summaryService;

    @RequestMapping(value = "/summary", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> saveSummary(@RequestBody SummaryDto req) {
        summaryService.addSummary(req);
        return ResponseEntity.ok().body("success");
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<SummaryDto> getSummary(@RequestParam(value = "agreementUserId", defaultValue = "") String agreementUserId,
                                                 @RequestParam(value = "year", defaultValue = "0") int year,
                                                 @RequestParam(value = "month", defaultValue = "0") int month) {
        Optional<User> userInfo = userService.findById(agreementUserId);
        if (userInfo.isPresent()) {
            Summary summary = summaryService.findBySummaryIdAndYearAndMonth(agreementUserId, year, month);
            if (summary != null) {
                SummaryDto summaryDto = new SummaryDto();
                summaryDto.setUserId(summary.getUserId());
                summaryDto.setYear(summary.getYear());
                summaryDto.setMonth(summary.getMonth());
                summaryDto.setText(summary.getText());
                summaryDto.setCreateDate(summary.getCreateDate());
                return ResponseEntity.ok().body(summaryDto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            throw new UserNotFoundException("User Not Found");
        }
    }
}
