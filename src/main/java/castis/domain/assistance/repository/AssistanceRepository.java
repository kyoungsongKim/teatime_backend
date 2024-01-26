package castis.domain.assistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import castis.domain.assistance.entity.Assistance;

@Repository
public interface AssistanceRepository extends JpaRepository<Assistance, Integer> {
}
