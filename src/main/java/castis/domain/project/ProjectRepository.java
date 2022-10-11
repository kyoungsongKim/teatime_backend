package castis.domain.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String>, JpaSpecificationExecutor<Project> {

    @Query(value = "SELECT site FROM project GROUP BY site" , nativeQuery = true)
    List<SiteInterface> findAllSiteInfo ();

    Optional<List<Project>> findAllBySite(String site);
}
