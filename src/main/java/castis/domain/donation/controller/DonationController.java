package castis.domain.donation.controller;

import castis.domain.donation.dto.DonationDto;
import castis.domain.donation.service.DonationService;
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
@RequestMapping(value = "/api")
public class DonationController {

    private final DonationService donationService;

    @RequestMapping(value = "/donation", method = RequestMethod.POST)
    public ResponseEntity saveDonationInfo(
            HttpServletRequest httpServletRequest
            , @RequestBody DonationDto donationDto
    ) throws Exception {
        log.info("request, uri[{}] DonationDto[{}]", httpServletRequest.getRequestURI(), donationDto);
        donationService.save(donationDto);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
