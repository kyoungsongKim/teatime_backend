package castis.domain.monthlysales.dto;

import castis.domain.monthlysales.entity.MonthlySales;
import castis.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class MonthlySalesDto {
    private Long id;

    private User user;

    private float salesAmount;

    private LocalDateTime summaryDate;

    public MonthlySalesDto(MonthlySales monthlySales) {
        this.id = monthlySales.getId();
        this.user = monthlySales.getUser();
        this.salesAmount = monthlySales.getSalesAmount();
        this.summaryDate = monthlySales.getSummaryDate();
    }

}
