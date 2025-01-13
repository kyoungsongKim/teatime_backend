package castis.domain.sign.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticationDto {
    private String userId;
    private String realName;
    private String teamName;
    private String position;
    private String cellphone;
    private String email;
    private String dailyReportList;
    private String vacationReportList;

    private String apiToken;

    private String accessToken;

    @Builder
    public AuthenticationDto(String userId, String realName,
                             String teamName, String position,
                             String cellphone, String email, String dailyReportList, String vacationReportList, String apiToken) {
        this.userId = userId;
        this.realName = realName;
        this.teamName = teamName;
        this.position = position;
        this.cellphone = cellphone;
        this.email = email;
        this.dailyReportList = dailyReportList;
        this.vacationReportList = vacationReportList;
        this.apiToken = apiToken;
        this.accessToken = apiToken;
    }
}
