package castis.domain.point;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Setter
@Getter
@NoArgsConstructor
public class PointHistoryDto {
    private Long id;
    private String createdDate;
    private String useDate;
    private String memo;
    private Integer point;
    private String sender;
    private String code;

    public PointHistoryDto (PointHistory pointHistory) {
        this.id = pointHistory.getId();
        this.createdDate = pointHistory.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.useDate = pointHistory.getUseDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.memo = pointHistory.getMemo();
        this.point = pointHistory.getPoint();
        this.sender = pointHistory.getSender();
        this.code = pointHistory.getCode();
    }
}
