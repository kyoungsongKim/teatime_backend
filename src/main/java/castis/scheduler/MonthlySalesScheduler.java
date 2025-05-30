package castis.scheduler;

import castis.domain.monthlysales.dto.CBankHistoryDto;
import castis.domain.monthlysales.dto.MonthlySalesDto;
import castis.domain.monthlysales.service.MonthlySalesService;
import castis.domain.user.entity.User;
import castis.domain.user.entity.UserDetails;
import castis.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MonthlySalesScheduler {

    private final UserService userService;
    private final MonthlySalesService monthlySalesService;

    @Scheduled(cron = "0 0 0 26 * *")
    public void findLastReflectMeetTime() {
        LocalDateTime today = LocalDateTime.now();
        List<User> userList = userService.getUserList();
        userList.forEach(user -> {
            try {
                UserDetails userDetails = user.getUserDetails();
                List<CBankHistoryDto> histories = monthlySalesService.getCBankHistory(user.getId(), today.getYear(),
                        today.getMonthValue());
                MonthlySalesDto monthlySalesDto = new MonthlySalesDto();
                monthlySalesDto.setUser(user);
                monthlySalesDto.setSummaryDate(today);
                float summary = 0;
                if (!histories.isEmpty()) {
                    summary = histories.stream()
                            .filter(history -> history.getRecvAccount().equals(userDetails.getCbankAccount()))
                            .map(history -> Float.parseFloat(history.getAmount())).reduce(
                                    summary, Float::sum);
                }
                monthlySalesDto.setSalesAmount(summary);
                monthlySalesService.createMonthlySales(monthlySalesDto);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}
