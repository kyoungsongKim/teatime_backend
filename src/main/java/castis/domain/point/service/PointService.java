package castis.domain.point.service;

import castis.domain.point.repository.PointHistoryRepository;
import castis.domain.point.dto.PointHistoryDto;
import castis.domain.point.entity.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
        String code = Long.toString(l, Character.MAX_RADIX);
        code = code.toUpperCase().substring(0,4);

        PointHistory pointHistory = new PointHistory(sender, receiver, point, memo, code);
        pointHistoryRepository.save(pointHistory);

        return code;
    }

    public ResponseEntity updatePointHistoryComplete(String code) {
        PointHistory pointHistory = pointHistoryRepository.findByCode(code).orElse(null);
        if(pointHistory != null) {
            pointHistory.setCode(code + "_COMPLETE");
            pointHistory.setUseDate(LocalDateTime.now());
            pointHistoryRepository.save(pointHistory);
            return new ResponseEntity(HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }


}
