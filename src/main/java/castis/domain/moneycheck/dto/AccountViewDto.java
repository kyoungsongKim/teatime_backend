package castis.domain.moneycheck.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class AccountViewDto {
    private String resultCode;
    private String resultMsg;
    private List<AccountDto> accountList;
}
