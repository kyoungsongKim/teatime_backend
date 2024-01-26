package castis.domain.assistance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import castis.domain.assistance.entity.AssistanceApply;

@Repository
public interface AssistanceApplyRepository extends JpaRepository<AssistanceApply, Integer> {

    List<AssistanceApply> findAllByApplierIdOrderByUpdatedDateDesc(String applierId);
}
