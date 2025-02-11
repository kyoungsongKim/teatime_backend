package castis.domain.assistance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AssistanceGroupDto {

    private Integer id;
    private String name;
    private Integer order;
    private List<AssistanceDto> services;

    public AssistanceGroupDto(Integer id, String name, Integer order, List<AssistanceDto> services) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.services = services;
    }
}
