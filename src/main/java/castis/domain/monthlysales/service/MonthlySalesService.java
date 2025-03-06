package castis.domain.monthlysales.service;

import castis.domain.monthlysales.dto.*;
import castis.domain.monthlysales.entity.MonthlySales;
import castis.domain.monthlysales.repository.MonthlySalesRepository;
import castis.domain.user.entity.User;
import castis.domain.user.entity.UserDetails;
import castis.domain.user.service.UserService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthlySalesService {

        @Value("${cbank.openApi.history.url}")
        private String cbankHistoryApiUrl;

        @Value("${cbank.openApi.otp.url}")
        private String cbankOtpApiUrl;

        @Value("${cbank.companyId}")
        private String cbankCompanyId;

        private final MonthlySalesRepository monthlySalesRepository;

        private final UserService userService;

        private WebClient historyClient;
        private WebClient otpClient;

        private final ObjectMapper objectMapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        @PostConstruct
        public void init() {
                historyClient = WebClient.builder()
                                .baseUrl(cbankHistoryApiUrl)
                                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .build();

                otpClient = WebClient.builder()
                                .baseUrl(cbankOtpApiUrl)
                                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .build();
        }

        public List<MonthlySalesDto> findAllByUsernameAndYear(String userName, int year) {
                LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
                LocalDateTime endDate = LocalDateTime.of(year + 1, 1, 1, 0, 0);
                List<MonthlySales> monthlySales = monthlySalesRepository
                                .findAllByUser_IdAndSummaryDateGreaterThanEqualAndSummaryDateLessThan(
                                                userName,
                                                startDate, endDate);
                return monthlySales.stream().map(MonthlySalesDto::new).collect(Collectors.toList());
        }

        public List<Integer> getYearListHasMonthlySales(String userName) {
            return monthlySalesRepository.findAllYearByUser_Id(userName);
        }

        public List<CBankHistoryDto> getCBankHistory(String userId, int year, int month)
                        throws EntityNotFoundException, IllegalArgumentException {

                User user = userService.getUser(userId);
                UserDetails userDetails = user.getUserDetails();
                if (userDetails == null) {
                        throw new IllegalArgumentException("user's details is empty");
                }
                if (userDetails.getCbankId() == null || userDetails.getCbankAccount() == null) {
                        throw new IllegalArgumentException("user's cbank information is empty");
                }
                CBankOtpRequestDto otpRequestBody = new CBankOtpRequestDto();
                otpRequestBody.setUserId(userDetails.getCbankId());
                otpRequestBody.setCompanyId(cbankCompanyId);

                CBankOtpResponseDto otpResponse = otpClient.post().bodyValue(otpRequestBody)
                                .retrieve()
                                .bodyToMono(CBankOtpResponseDto.class)
                                .block();

                CBankHistoryRequestDto historyRequestBody = new CBankHistoryRequestDto();
                historyRequestBody.setAccountId(userDetails.getCbankAccount());
                historyRequestBody.setUserId(userDetails.getCbankId());

                LocalDateTime end = LocalDateTime.of(year, month, 25, 0, 0);
                LocalDateTime start = end.minusMonths(1).plusDays(1);

                String startDate = String.format("%04d%02d%02d", start.getYear(), start.getMonthValue(),
                                start.getDayOfMonth());
                String endDate = String.format("%04d%02d%02d", end.getYear(), end.getMonthValue(), end.getDayOfMonth());
                historyRequestBody.setStartDate(startDate);
                historyRequestBody.setEndDate(endDate);
                historyRequestBody.setOtp(otpResponse.getOtp());

                CBankHistoryResponseDto historyResponse = historyClient.post().bodyValue(historyRequestBody)
                                .retrieve()
                                .bodyToMono(CBankHistoryResponseDto.class)
                                .block();

                return historyResponse.getHistory();
        }

        // create
        public void createMonthlySales(MonthlySalesDto monthlySalesDto) {
                MonthlySales monthlySales = new MonthlySales();
                monthlySales.setUser(userService.getUser(monthlySalesDto.getUser().getId()));
                monthlySales.setSalesAmount(monthlySalesDto.getSalesAmount());
                monthlySales.setSummaryDate(monthlySalesDto.getSummaryDate());
                monthlySalesRepository.save(monthlySales);
        }
}
