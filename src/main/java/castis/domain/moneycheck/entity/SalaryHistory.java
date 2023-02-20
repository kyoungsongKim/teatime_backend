package castis.domain.moneycheck.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "salary_history")
@Entity
@NoArgsConstructor
public class SalaryHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer year;

    @Column
    private Integer month;

    @Column
    private String name;

    @Column
    private String coupleAccount;

    @Column
    private String bankBranch;

    @Column
    private String corporation;

    @Column
    private String withdrawalCurrency;

    @Column
    private String transferHistory;

    @Column
    private String sender;

    @Column
    private String amountOfMonth;

    @Column
    private String amountForSalary;

    @Column
    private LocalDateTime createDate;
}
