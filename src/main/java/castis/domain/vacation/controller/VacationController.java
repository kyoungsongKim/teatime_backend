package castis.domain.vacation.controller;

import castis.domain.user.entity.UserDetails;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import castis.domain.security.jwt.AuthProvider;
import castis.domain.user.CustomUserDetails;
import castis.domain.user.entity.User;
import castis.domain.user.service.UserService;
import castis.domain.vacation.dto.CreateVacationBody;
import castis.domain.vacation.dto.MyVacationResponse;
import castis.domain.vacation.dto.UpdateVacationBody;
import castis.domain.vacation.dto.VacationHistoryDto;
import castis.domain.vacation.dto.VacationInfoDto;
import castis.domain.vacation.entity.VacationHistory;
import castis.domain.vacation.service.VacationHistoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/vacation")
public class VacationController {

    private final UserService userService;
    private final VacationHistoryService vacationHistoryService;
    private final AuthProvider authProvider;

    @GetMapping("")
    public ResponseEntity<MyVacationResponse> getVacationInfo(HttpServletRequest httpServletRequest,
            @RequestParam(name = "userId", required = true) String userId,
            @RequestParam(name = "workedYear", required = false) Byte workedYear) {
        if(userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String id = authProvider.getUserIdFromRequest(httpServletRequest);
        boolean isAdmin = authProvider.isAdmin(httpServletRequest);

        User foundUser = userService.getUser(userId);
        UserDetails userDetails = foundUser.getUserDetails();
        if (userDetails == null || userDetails.getRenewalDate() == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        if (!isAdmin && !(foundUser.getId().equals(id))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        LocalDate renewalDate = userDetails.getRenewalDate();

        List<Byte> workedYearList = new ArrayList<Byte>();
        int currentYear = Year.now().getValue();
        LocalDate currentRenewalDate = renewalDate.withYear(currentYear);
        boolean isRenewed = currentRenewalDate.isBefore(LocalDate.now());
        byte fullyWorkedYear = (byte) (Year.now().getValue() - renewalDate.getYear() + (isRenewed ? 1 : 0));
        if (fullyWorkedYear < 1) {
            workedYearList.add((byte) 1);
        } else {
            for (byte y = 1; y <= fullyWorkedYear; y++) {
                workedYearList.add(y);
            }
        }
        if (workedYear == null) {
            workedYear = workedYearList.get(workedYearList.size() - 1);
        }

        LocalDate startDate = renewalDate.plusYears(workedYear - 1);
        LocalDate endDate = startDate.plusYears(1);
        List<VacationHistoryDto> histories = vacationHistoryService.getVacationHistoryList(foundUser.getId(),
                startDate.atStartOfDay(), endDate.atStartOfDay(), false);

        MyVacationResponse result = new MyVacationResponse();
        VacationInfoDto vacationInfo = vacationHistoryService.getVacationInfo(userId,
                startDate.atStartOfDay(), false);
        if (vacationInfo != null) {
            result.setLeft(vacationInfo.getLeft());
            result.setUsed(vacationInfo.getUsed());
            result.setTotal(vacationInfo.getTotal());
        }

        result.setUserId(userId);
        result.setWorkedYearList(workedYearList);
        result.setRenewalDate(renewalDate);
        result.setHistories(histories);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/list")
    public ResponseEntity<List<VacationHistoryDto>> getVacationListByYear(HttpServletRequest httpServletRequest,
                                                                    @RequestParam(name = "userId") String userId,
                                                                    @RequestParam(name = "year") Integer year) {
        String id = authProvider.getUserIdFromRequest(httpServletRequest);
        boolean isAdmin = authProvider.isAdmin(httpServletRequest);

        User foundUser = userService.getUser(userId);
        UserDetails userDetails = foundUser.getUserDetails();
        if (userDetails == null || userDetails.getRenewalDate() == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        if (!isAdmin && !(foundUser.getId().equals(id))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<VacationHistoryDto> vacationHistoryDtos = vacationHistoryService.getVacationHistoryListByUserIdAndYear(userId, year);


        return ResponseEntity.ok().body(vacationHistoryDtos);
    }

    @GetMapping("/all")
    public ResponseEntity<List<VacationInfoDto>> getVacationInfo(HttpServletRequest httpServletRequest) {
        boolean isAdmin = authProvider.isAdmin(httpServletRequest);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        CustomUserDetails userDetails = authProvider.getUserDetails(httpServletRequest);
        if (userDetails.getRoles().contains("ROLE_ADMIN")) {
            User user = userService.getUser(userDetails.getUserId());
            List<VacationInfoDto> result = vacationHistoryService.getAllVacationInfoByTeamName(LocalDateTime.now(), false, user.getTeamName());
            return ResponseEntity.ok().body(result);
        } else if (userDetails.getRoles().contains("ROLE_SUPER_ADMIN")) {
            List<VacationInfoDto> result = vacationHistoryService.getAllVacationInfo(LocalDateTime.now(), false);
            return ResponseEntity.ok().body(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    public ResponseEntity createVacation(HttpServletRequest httpServletRequest,
            @RequestBody CreateVacationBody createVacationBody) {
        User foundUser = userService.getUser(createVacationBody.getUserId());
        if (foundUser == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("User Not Found");
        }

        boolean isAdmin = authProvider.isAdmin(httpServletRequest);

        VacationInfoDto vacationInfo = vacationHistoryService.getVacationInfo(createVacationBody.getUserId(),
                createVacationBody.getEventStartDate(), false);

        System.out.println(createVacationBody.getAmount() + " " + vacationInfo.getLeft());
        if (createVacationBody.getAmount() > vacationInfo.getLeft()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Not enough vacation");
        }
        VacationHistory vacationHistory = new VacationHistory();
        vacationHistory.setEventStartDate(createVacationBody.getEventStartDate());
        vacationHistory.setEventEndDate(createVacationBody.getEventEndDate());
        vacationHistory.setUserId(createVacationBody.getUserId());
        vacationHistory.setReason(createVacationBody.getReason());
        vacationHistory.setType(createVacationBody.getType());
        vacationHistory.setAmount(createVacationBody.getAmount());
        if (isAdmin) {
            vacationHistory.setAdminMemo(createVacationBody.getAdminMemo());
        }
        vacationHistoryService.create(vacationHistory);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateVacation(HttpServletRequest httpServletRequest,
            @RequestBody UpdateVacationBody updateVacationBody,
            @PathVariable(value = "id") Long id) {

        User foundUser = userService.getUser(updateVacationBody.getUserId());
        if (foundUser == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("User Not Found");
        }

        boolean isAdmin = authProvider.isAdmin(httpServletRequest);
        VacationHistoryDto targetVacationHistory = vacationHistoryService.getById(id);
        VacationInfoDto vacationInfo = vacationHistoryService.getVacationInfo(updateVacationBody.getUserId(),
                LocalDateTime.now(), false);

        if (updateVacationBody.getAmount() - targetVacationHistory.getAmount() > vacationInfo.getLeft()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Not enough vacation");
        }
        VacationHistory vacationHistory = new VacationHistory(targetVacationHistory);
        vacationHistory.setEventStartDate(updateVacationBody.getEventStartDate());
        vacationHistory.setEventEndDate(updateVacationBody.getEventEndDate());
        vacationHistory.setReason(updateVacationBody.getReason());
        vacationHistory.setType(updateVacationBody.getType());
        vacationHistory.setAmount(updateVacationBody.getAmount());
        if (isAdmin) {
            vacationHistory.setAdminMemo(updateVacationBody.getAdminMemo());
        }
        vacationHistoryService.update(vacationHistory);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity updateVacation(HttpServletRequest httpServletRequest,
            @PathVariable(value = "id") Long id) {
        boolean isAdmin = authProvider.isAdmin(httpServletRequest);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        vacationHistoryService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<VacationHistoryDto> getVacation(HttpServletRequest httpServletRequest,
            @PathVariable(value = "id") Long id) {
        String token = httpServletRequest.getHeader("Authorization");
        CustomUserDetails user = (CustomUserDetails) authProvider.getAuthentication(token).getPrincipal();

        // 정보가 없는 사용자는 조회 불가
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        VacationHistoryDto result = vacationHistoryService.getById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
