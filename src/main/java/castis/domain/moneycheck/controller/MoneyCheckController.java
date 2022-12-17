package castis.domain.moneycheck.controller;

import castis.domain.moneycheck.dto.MoneyCheckDto;
import castis.domain.moneycheck.service.MoneyCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/money")
public class MoneyCheckController {

    private final MoneyCheckService moneyCheckService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity getMoneyCheckList(HttpServletRequest httpServletRequest) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        List<MoneyCheckDto> moneyCheckDtoList = moneyCheckService.findAll();
        if (moneyCheckDtoList != null) {
            Iterator<MoneyCheckDto> mcIterator = moneyCheckDtoList.iterator();
            while(mcIterator.hasNext()){
                MoneyCheckDto curDto = mcIterator.next();
                if(curDto!=null){
                    if (curDto.getTeamName().contains("티타임") == false ) {
                        mcIterator.remove();
                    }
                }
            }
            return new ResponseEntity<>(moneyCheckDtoList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }
}