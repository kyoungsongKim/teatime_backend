package castis.domain.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class ReportRequestDto {
    private String sendUserName;
    private String title;
    private String senderEmail;
    private String receiveEmail;
    private String contents;
}
