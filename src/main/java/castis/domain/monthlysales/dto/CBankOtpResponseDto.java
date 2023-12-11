package castis.domain.monthlysales.dto;

import lombok.Data;

@Data
public class CBankOtpResponseDto {
    private int resultCode;
    private String resultMsg;
    private String now;
    private String otp;
}
