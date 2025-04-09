package castis.domain.monthlysales.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CBankHistoryRequestDto {
    private String userId;
    private String userPwd;
    private String accountId;
    private String duration;
    private String startDate;
    private String endDate;
    private String otp;
}
