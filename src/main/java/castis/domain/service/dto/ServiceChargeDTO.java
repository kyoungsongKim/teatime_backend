package castis.domain.service.dto;

import castis.domain.service.entity.ServiceUserRelation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ServiceChargeDTO {
    private String serviceName;
    private Integer charge;

    public ServiceChargeDTO (ServiceUserRelation serviceUserRelation) {
        this.serviceName = serviceUserRelation.getService().getName();
        this.charge = serviceUserRelation.getServiceCharge();
    }
}
