package castis.domain.project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OtpDTO {
    private String resultCode;
    private String resultMsg;
    private String otp;
}
