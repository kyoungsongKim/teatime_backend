package castis.domain.service.repository;

import castis.domain.service.entity.ServiceUserRelation;
import castis.domain.service.entity.ServiceUserRelationPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceUserRelationRepository extends JpaRepository<ServiceUserRelation, ServiceUserRelationPK> {

    @Query(value = "SELECT * FROM service_user_relation WHERE user_id = :userId" , nativeQuery = true)
    List<ServiceUserRelation> findAllByUserId(String userId);
}
