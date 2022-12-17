package castis.domain.moneycheck.service;

import castis.domain.moneycheck.dto.MoneyCheckDto;
import castis.domain.point.dto.PointHistoryDto;
import castis.domain.point.entity.PointHistory;
import castis.domain.project.dto.OtpDTO;
import castis.domain.project.dto.TransferDTO;
import castis.domain.service.dto.ServiceChargeDTO;
import castis.domain.service.dto.ServiceDTO;
import castis.domain.service.service.ServiceService;
import castis.domain.user.entity.User;
import castis.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class MoneyCheckService {

    private final ServiceService serviceService;
    private final UserRepository userRepository;
    private final Environment environment;

    public List<MoneyCheckDto> findAll(){
        List<MoneyCheckDto> result = new ArrayList<>();

        List<User> userList = userRepository.findAll();
        List<ServiceChargeDTO> serviceChargeList = serviceService.getAllServiceListWithCharge();
        userList.forEach(user -> {
            String userId = user.getId();
            MoneyCheckDto mcd = new MoneyCheckDto(user);
            long needMoney = 0L;
            for ( ServiceChargeDTO curService : serviceChargeList) {
                if ( curService.getUserId().equalsIgnoreCase(userId)) {
                    switch ( curService.getServiceId() ) {
                        case 1: // default RS
                            mcd.setDefaultRSrate(curService.getCharge());
                            break;
                        case 2:
                            mcd.setAgreeEndDate(curService.getCharge());
                            break;
                        case 3:
                            mcd.setSelfDecidedMoney(curService.getCharge());
                            needMoney += parseHangulToLongCost(curService.getCharge());
                            break;
                        case 6:
                            mcd.setEmergencyPersonService(curService.getCharge());
                            break;
                        case 7:
                            mcd.setSpaceService(curService.getCharge());
                            needMoney += parseHangulToLongCost(curService.getCharge());
                            break;
                        case 10:
                            mcd.setVacationManageService(curService.getCharge());
                            needMoney += parseHangulToLongCost(curService.getCharge());
                            break;
                        case 11:
                            mcd.setHealthCareService(curService.getCharge());
                            needMoney += parseHangulToLongCost(curService.getCharge());
                            break;
                        case 13:
                            mcd.setPresentService(curService.getCharge());
                            needMoney += parseHangulToLongCost(curService.getCharge());
                            break;
                        case 20:
                            mcd.setGatePortalService(curService.getCharge());
                            needMoney += parseHangulToLongCost(curService.getCharge());
                            break;
                        case 21:
                            mcd.setCorporationService(curService.getCharge());
                            needMoney += parseHangulToLongCost(curService.getCharge());
                            break;
                        case 22:
                            mcd.setFamilyEventService(curService.getCharge());
                            needMoney += parseHangulToLongCost(curService.getCharge());
                            break;
                        default:
                            break;
                    }
                }
            }
            mcd.setNeedMoney(String.valueOf(needMoney));
            result.add(mcd);
        });
        return result;
    }

    private long parseHangulToLongCost(String koreanStringMoney) {
        long cost = 0L;
        if (koreanStringMoney == null || koreanStringMoney.isEmpty()) {
            log.error("[calculateNeedMoney] koreanStringMoney is null: {}");
            return 0L;
        }
        try {
            String selfDecidedMoney = koreanStringMoney;
            String nospaceMoney = selfDecidedMoney.replaceAll(" ","").replaceAll(",","");
            String onlyDigitString = nospaceMoney.replace("만","0000");
            if (onlyDigitString.toUpperCase().contains("CAS") == true) {
                onlyDigitString = onlyDigitString.substring(0, onlyDigitString.indexOf("CAS"));
            }
            cost = Long.parseLong(onlyDigitString);
        } catch ( Exception e ) {
            return 0L;
        }
        return cost;
    }
}
