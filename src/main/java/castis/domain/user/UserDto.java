package castis.domain.user;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * A DTO for the {@link User} entity
 */
@Setter
@Getter
@NoArgsConstructor
public class UserDto implements Serializable {
    private String id;
    private String username;
    private String realName;
    private Boolean enabled;
    private String teamName;
    private String position;
    private String cellphone;
    private String email;
    private String dailyReportList;
    private String vacationReportList;
}