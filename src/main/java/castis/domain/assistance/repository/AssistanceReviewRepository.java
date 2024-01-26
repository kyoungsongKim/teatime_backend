package castis.domain.assistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import castis.domain.assistance.entity.AssistanceReview;

@Repository
public interface AssistanceReviewRepository extends JpaRepository<AssistanceReview, Integer> {

}
