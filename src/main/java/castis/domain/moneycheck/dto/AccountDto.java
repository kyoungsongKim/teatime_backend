package castis.domain.moneycheck.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class AccountDto {
    private String id;
    private String type;
    private String branchName;
    private String name;
    private String currency;
    private String password;
    private long balance;
    private Date createDate;
}
