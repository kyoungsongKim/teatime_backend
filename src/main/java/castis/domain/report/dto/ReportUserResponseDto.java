package castis.domain.report.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class ReportUserResponseDto {
    private String sendUserName;
    private String senderEmail;
    private String receiveEmail;
}
