package castis.domain.monthlysales.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CBankOtpRequestDto {
    private String userId;
    private String companyId;
}
