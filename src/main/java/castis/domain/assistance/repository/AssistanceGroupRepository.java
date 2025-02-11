package castis.domain.assistance.repository;

import castis.domain.assistance.entity.AssistanceGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssistanceGroupRepository extends JpaRepository<AssistanceGroup, Integer> {
}
