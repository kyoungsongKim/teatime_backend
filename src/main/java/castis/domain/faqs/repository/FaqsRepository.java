package castis.domain.faqs.repository;

import castis.domain.faqs.entity.Faqs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqsRepository extends JpaRepository<Faqs, Integer>, JpaSpecificationExecutor<Faqs> {
}
