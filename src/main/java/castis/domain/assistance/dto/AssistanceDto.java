package castis.domain.assistance.dto;

import castis.domain.assistance.entity.Assistance;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AssistanceDto {
    private Integer id;

    private String name;

    private String description;

    private String form;

    private Integer price;

    private Integer groupId;

    public AssistanceDto(Assistance assistanceService) {
        this.id = assistanceService.getId();
        this.name = assistanceService.getName();
        this.description = assistanceService.getDescription();
        this.form = assistanceService.getForm();
        this.price = assistanceService.getPrice();
        this.groupId = assistanceService.getGroup() == null ? null : assistanceService.getGroup().getId();
    }
}
