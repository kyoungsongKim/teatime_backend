package castis.domain.moneycheck.dto;

import castis.domain.point.entity.PointHistory;
import castis.domain.service.entity.Service;
import castis.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class MoneyCheckDto {
    private String id;
    private String userId;
    private String userName;
    private String realName;
    private String teamName;
    private String email;
    private String cbankAccount;
    //default rate
    private String defaultRSrate;
    //last date of agreement
    private String agreeEndDate;
    //self decided money
    private String selfDecidedMoney;
    //emergency person service
    private String emergencyPersonService;
    // space service
    private String spaceService;
    // vacation manage service
    private String vacationManageService;
    // health care service
    private String healthCareService;
    // present service(moon day, thanksgiving)
    private String presentService;
    // gate/portal service
    private String gatePortalService;
    // corporation service
    private String corporationService;
    // family event service
    private String familyEventService;

    private String needMoney;
    private String currentAccountMoney;

    public MoneyCheckDto(User user) {
        this.id = user.getId();
        this.userId = user.getUserName();
        this.userName = user.getUserName();
        this.realName = user.getRealName();
        this.teamName = user.getTeamName();
        this.email = user.getEmail();
        this.cbankAccount = user.getCbankAccount();
        this.defaultRSrate="";
        this.selfDecidedMoney="";
        this.emergencyPersonService="X";
        this.spaceService="X";
        this.vacationManageService="X";
        this.healthCareService="X";
        this.presentService="X";
        this.gatePortalService="X";
        this.corporationService="X";
        this.familyEventService="X";
    }
}
