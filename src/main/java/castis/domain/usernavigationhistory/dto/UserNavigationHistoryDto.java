package castis.domain.usernavigationhistory.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class UserNavigationHistoryDto implements Serializable {
    public String menuName;
    public String userAgent;
}
