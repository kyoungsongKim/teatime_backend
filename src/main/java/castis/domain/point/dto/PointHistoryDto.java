package castis.domain.point.dto;

import castis.domain.point.entity.PointHistory;
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
    private Integer expValue;
    private String sender;
    private String recver;
    private String code;

    public PointHistoryDto (PointHistory pointHistory) {
        this.id = pointHistory.getId();
        this.createdDate = pointHistory.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(pointHistory.getUseDate() != null) {
            this.useDate = pointHistory.getUseDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        this.memo = pointHistory.getMemo();
        this.point = pointHistory.getPoint();
        this.sender = pointHistory.getSender();
        this.recver = pointHistory.getRecver();
        this.code = pointHistory.getCode();
        this.expValue = pointHistory.getExpvalue();
    }
}
