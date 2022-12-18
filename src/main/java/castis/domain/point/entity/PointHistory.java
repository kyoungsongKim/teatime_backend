package castis.domain.point.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "point_history")
@Entity
@NoArgsConstructor
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer point;
    @Column
    private Integer expvalue;

    @Column
    private String code;

    @Column
    private String memo;

    @Column
    private String sender;

    @Column
    private String recver;

    @Column
    private LocalDateTime createDate;

    @Column
    private LocalDateTime useDate;

    public PointHistory (String sender, String receiver, Integer point, Integer expvalue, String memo, String code) {
        this.sender = sender;
        this.recver = receiver;
        this.point = point;
        this.memo = memo;
        this.code = code;
        this.expvalue = expvalue;
        this.createDate = LocalDateTime.now();
    }
}
