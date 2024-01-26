package castis.domain.assistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import castis.domain.assistance.entity.AssistanceSuggestion;

@Repository
public interface AssistanceSuggestionRepository extends JpaRepository<AssistanceSuggestion, Integer> {

}
