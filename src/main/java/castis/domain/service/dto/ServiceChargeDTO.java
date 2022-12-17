package castis.domain.service.dto;

import castis.domain.service.entity.ServiceUserRelation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ServiceChargeDTO {
    private String userId;
    private int serviceId;
    private String serviceName;
    private String charge;

    public ServiceChargeDTO (ServiceUserRelation serviceUserRelation) {
        this.serviceId = serviceUserRelation.getService().getId();
        this.serviceName = serviceUserRelation.getService().getName();
        this.charge = serviceUserRelation.getServiceCharge();
        this.userId = serviceUserRelation.getUser().getId();
    }
}
