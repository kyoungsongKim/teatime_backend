package castis.domain.monthlysales.controller;

import castis.domain.monthlysales.dto.CBankHistoryDto;
import castis.domain.monthlysales.service.MonthlySalesService;
import castis.domain.user.entity.User;
import castis.domain.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/monthly-sales")
public class MonthlySalesController {

    private final MonthlySalesService monthlySalesService;
    private final UserService userService;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public ResponseEntity<String> getCurrentSales(HttpServletRequest httpServletRequest, @RequestParam String userId)
            throws JsonProcessingException {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        LocalDateTime today = LocalDateTime.now();
        List<CBankHistoryDto> result = monthlySalesService.getCBankHistory(userId, today.getYear(),
                today.getMonthValue());
        User user = userService.getUser(userId);
        float totalSales = 0f;
        if (result.size() > 0) {
            totalSales = result.stream().filter(history -> history.getRecvAccount().equals(user.getCbankAccount()))
                    .map(history -> Float.parseFloat(history.getAmount())).reduce(
                            totalSales, (a, b) -> a + b);
        }
        return new ResponseEntity<String>(String.format("%.2f CAS", totalSales), HttpStatus.OK);
    }
}
