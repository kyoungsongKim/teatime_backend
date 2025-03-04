package castis.domain.point.service;

import castis.domain.donation.dto.DonationDto;
import castis.domain.donation.service.DonationService;
import castis.domain.point.dto.PointHistoryDto;
import castis.domain.point.entity.PointHistory;
import castis.domain.point.repository.PointHistoryRepository;
import castis.domain.project.dto.OtpDTO;
import castis.domain.project.dto.TransferDTO;
import castis.domain.user.entity.User;
import castis.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;
    private final Environment environment;
    private final DonationService donationService;

    private static final String CHARSET = "123456789ABCDEFGHJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    public List<PointHistoryDto> findAllPointHistoryByRecver(String recver, String periodYear){
        List<PointHistoryDto> result = new ArrayList<>();

        Specification<PointHistory> spec = (con, query, cb) -> {
            // ordering
            List<Order> orders = new ArrayList<>();
            //orders.add(cb.asc(con.get("seq")));
            orders.add(cb.desc(con.get("createDate")));
            query.orderBy(orders);

            List<Predicate> predicates = new ArrayList<>();
            if (recver != null && !"".equals(recver)) {
                predicates.add(cb.equal(con.get("recver"), recver));
            }
            if (periodYear != null && !"".equals(periodYear)) {
                LocalDateTime startDate = LocalDateTime.of(Integer.parseInt(periodYear), 1, 1, 0, 0);
                LocalDateTime endDate = LocalDateTime.of(Integer.parseInt(periodYear), 12, 31, 23, 59);
                predicates.add(cb.between(con.get("createDate"), startDate, endDate));
            }

            return predicates.size() > 0 ? cb.and(predicates.toArray(new Predicate[predicates.size()])) : null;
        };
        List<PointHistory> list = pointHistoryRepository.findAll(spec).stream().collect(Collectors.toList());
        list.forEach(i -> {
            result.add(new PointHistoryDto(i));
        });

        return result;
    }

    public List<PointHistoryDto> findAllPointHistory(){
        List<PointHistoryDto> result = new ArrayList<>();

        List<PointHistory> list = pointHistoryRepository.findAll();
        list.forEach(i -> {
            result.add(new PointHistoryDto(i));
        });

        return result;
    }

    public PointHistory getPointHistoryByCode(String code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            List<PointHistory> pointHistoryList = pointHistoryRepository.findByCodeAndRecver(code.toUpperCase(), username).orElse(null);
            if(pointHistoryList != null && pointHistoryList.size() > 0) {
                PointHistory pointHistory = pointHistoryList.get(0);
                return pointHistory;
            }
        }
        return null;
    }

    public PointHistory getLastReceivePointHistory(String recver) {
        List<PointHistory> pointHistoryList = pointHistoryRepository.findAllByRecverOrderByCreateDateDesc(recver).orElse(null);
        if(pointHistoryList != null && pointHistoryList.size() > 0) {
            for ( PointHistory ph : pointHistoryList ) {
                if ( (ph.getCode().equalsIgnoreCase("LEVEL_UP") || ph.getCode().equalsIgnoreCase("AUTO")) == false) {
                    return ph;
                }
            }
        }
        return null;
    }

    public List<PointHistoryDto> getPointHistoryByYearAndMonth(Integer year, Integer month) {
        List<PointHistoryDto> result = new ArrayList<>();
        List<PointHistory> pointHistoryList = pointHistoryRepository.findAllByYearAndMonth(year, month);
        pointHistoryList.forEach(i -> {
            result.add(new PointHistoryDto(i));
        });
        return result;
    }

    public String savePointHistoryAndCodeReturn(String sender, String receiver, Integer point, String memo) {
        String code;
        List<PointHistory> pointHistoryList;

        do {
            code = generateRandomCode();
            pointHistoryList = pointHistoryRepository.findByCodeAndRecver(code, receiver).orElse(null);
        } while (pointHistoryList != null && !pointHistoryList.isEmpty());

        PointHistory pointHistory = new PointHistory(sender, receiver, point, 0, memo, code);
        if (point < 0) {
            pointHistory.setUseDate(LocalDateTime.now());
            pointHistory.setCode(code + "_COMPLETE");
        }

        pointHistoryRepository.save(pointHistory);
        return code;
    }

    public void changePointToExp(String sender, String receiver, Integer point, String memo) {
        int minusPoint = point * -1;
        int plusPoint = point.intValue();
        int currentPoint = getPointByUserId(receiver);
        if ( plusPoint > currentPoint ) {
            if ( currentPoint > 0) {
                plusPoint = currentPoint;
                minusPoint = plusPoint * -1;
            } else {
                plusPoint = 0;
            }
        }
        PointHistory pointHistory = new PointHistory(sender, receiver, minusPoint, plusPoint, memo, "LEVEL_UP");
        pointHistory.setUseDate(LocalDateTime.now());
        pointHistoryRepository.save(pointHistory);
    }

    private int getPointByUserId(String userId) {
        int totalPoint = 0;
        List<PointHistoryDto> pointHistoryDtoList = findAllPointHistory();
        if (pointHistoryDtoList != null) {
            Iterator<PointHistoryDto> phIterrator = pointHistoryDtoList.iterator();
            while (phIterrator.hasNext()) {
                PointHistoryDto curDto = phIterrator.next();
                if (curDto != null) {
                    if (curDto.getRecver().equalsIgnoreCase(userId) && curDto.getUseDate() != null) {
                        totalPoint += curDto.getPoint();
                    }
                }
            }
        }
        return totalPoint;
    }

    @Transactional
    public ResponseEntity updatePointHistoryComplete(String code, String userName) throws Exception {
        code = code.toUpperCase();
        if (code.contains("LEVEL_UP") || code.contains("AUTO")) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        List<PointHistory> pointHistoryList = pointHistoryRepository.findByCodeAndRecver(code.toUpperCase(), userName).orElse(null);
        if(pointHistoryList != null && pointHistoryList.size() > 0) {
            PointHistory pointHistory = pointHistoryList.get(0);

            //cbank 기준 sender
            User sender = userRepository.findById(pointHistory.getRecver()).orElse(null);
            if(sender != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", sender.getCbankId());
                jsonObject.put("companyId", environment.getProperty("cbank.companyId"));
                ResponseEntity<OtpDTO> res = new RestTemplate().postForEntity(environment.getProperty("cbank.openApi.otp.url")
                        , jsonObject, OtpDTO.class);
                //cbank 기준 receiver
                User receiver = userRepository.findById(pointHistory.getSender()).orElse(null);
                if(receiver != null) {
                    jsonObject = new JSONObject();
                    jsonObject.put("userId", sender.getCbankId());
                    DonationDto donationDto = donationService.findByCbankId(sender.getCbankId());
                    if (donationDto != null) {
                        jsonObject.put("sendAccountPwd", donationDto.getCbankPass());
                        jsonObject.put("recvAccountId", donationDto.getDonationAccount());
                    }
                    jsonObject.put("sendAccountId", sender.getCbankAccount());
                    jsonObject.put("amount", pointHistory.getPoint());
                    jsonObject.put("transferHistory", "기부금(" + pointHistory.getMemo() +")");
                    jsonObject.put("otp", res.getBody().getOtp());
                    jsonObject.put("memo", "");

                    ResponseEntity<TransferDTO> result = new RestTemplate().postForEntity(environment.getProperty("cbank.openApi.transfer.url")
                            , jsonObject, TransferDTO.class);

                    if(!result.getBody().getResultCode().equals("200")) {
                        new ResponseEntity(result.getBody().getResultMsg(), HttpStatus.BAD_REQUEST);
                    }
                    pointHistory.setCode(code + "_COMPLETE");
                    pointHistory.setPoint(pointHistory.getPoint() * 100);
                    pointHistory.setUseDate(LocalDateTime.now());
                    pointHistoryRepository.save(pointHistory);
                    return new ResponseEntity(HttpStatus.OK);
                }
            }
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    private String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
        }
        return code.toString();
    }
}
