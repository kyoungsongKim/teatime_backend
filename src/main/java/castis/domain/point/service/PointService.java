package castis.domain.point.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;
    private final Environment environment;

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

    public String savePointHistoryAndCodeReturn(String sender, String receiver, Integer point, String memo) {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        List<PointHistory> pointHistoryList = null;
        String code = "";
        do {
            code = Long.toString(l, Character.MAX_RADIX);
            code = code.toUpperCase().substring(0,4);
            pointHistoryList = pointHistoryRepository.findByCodeEquals(code.toUpperCase()).orElse(null);
        } while (pointHistoryList == null);
        PointHistory pointHistory = new PointHistory(sender, receiver, point, memo, code);
        pointHistoryRepository.save(pointHistory);
        return code;
    }

    @Transactional
    public ResponseEntity updatePointHistoryComplete(String code) throws Exception {
        List<PointHistory> pointHistoryList = pointHistoryRepository.findByCodeEquals(code.toUpperCase()).orElse(null);
        if(pointHistoryList != null) {
            PointHistory pointHistory = pointHistoryList.get(0);
            pointHistory.setCode(code + "_COMPLETE");
            pointHistory.setUseDate(LocalDateTime.now());
            pointHistoryRepository.save(pointHistory);

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
                    jsonObject.put("sendAccountId", sender.getCbankAccount());
                    jsonObject.put("recvAccountId", "0379-0201");
                    jsonObject.put("amount", pointHistory.getPoint());
                    jsonObject.put("transferHistory", "기부 포인트");
                    jsonObject.put("otp", res.getBody().getOtp());
                    jsonObject.put("memo", pointHistory.getMemo());

                    ResponseEntity<TransferDTO> result = new RestTemplate().postForEntity(environment.getProperty("cbank.openApi.transfer.url")
                            , jsonObject, TransferDTO.class);

                    if(!result.getBody().getResultCode().equals("200")) {
                        new ResponseEntity(result.getBody().getResultMsg(), HttpStatus.BAD_REQUEST);
                    }
                }
            }
            return new ResponseEntity(HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }


}
