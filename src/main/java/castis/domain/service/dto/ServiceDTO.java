package castis.domain.service.dto;

import castis.domain.service.entity.Service;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ServiceDTO {
    private Integer id;
    private String name;
    private String desc;

    public ServiceDTO (Service service) {
        this.id = service.getId();
        this.name = service.getName();
        this.desc = service.getDescription();
    }
}
