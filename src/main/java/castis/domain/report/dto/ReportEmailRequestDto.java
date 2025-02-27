package castis.domain.report.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ReportEmailRequestDto {

    private String sendUserName;
    private String title;
    private String contents;
    private List<String> receiveEmail;
}
