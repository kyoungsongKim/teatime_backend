package castis.domain.service.service;

import castis.domain.service.dto.ServiceChargeDTO;
import castis.domain.service.dto.ServiceDTO;
import castis.domain.service.entity.ServiceUserRelation;
import castis.domain.service.repository.ServiceRepository;
import castis.domain.service.repository.ServiceUserRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceUserRelationRepository serviceUserRelationRepository;

    public List<ServiceDTO> getServiceList() {
        List<castis.domain.service.entity.Service> serviceList = serviceRepository.findAll();
        List<ServiceDTO> result = new ArrayList<>();
        for(castis.domain.service.entity.Service service : serviceList) {
            result.add(new ServiceDTO(service));
        }

        return result;
    }

    public List<ServiceChargeDTO> getServiceListWithCharge(String userId) {
        List<ServiceUserRelation> serviceUserRelationList = serviceUserRelationRepository.findAllByUserId(userId);
        List<ServiceChargeDTO> result = new ArrayList<>();
        for(ServiceUserRelation serviceUserRelation : serviceUserRelationList) {
            result.add(new ServiceChargeDTO(serviceUserRelation));
        }
        return result;
    }

}
