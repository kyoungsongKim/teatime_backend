package castis.domain.statistics.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MonthlyStatisticsDto {
    private String site;
    private String projectName;
    private Float sum;
    private String bgColor;

    public MonthlyStatisticsDto(MonthlyStatisticsInterface monthlyStatisticsInterface) {
        this.site = monthlyStatisticsInterface.getSite();
        this.projectName = monthlyStatisticsInterface.getProjectName();
        this.sum = monthlyStatisticsInterface.getSum();
        this.bgColor = monthlyStatisticsInterface.getBgColor();
    }
}
