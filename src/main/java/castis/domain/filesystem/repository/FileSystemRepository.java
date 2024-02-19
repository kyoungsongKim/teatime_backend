package castis.domain.filesystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import castis.domain.filesystem.entity.FileMeta;

@Repository
public interface FileSystemRepository extends JpaRepository<FileMeta, Long>, JpaSpecificationExecutor<FileMeta> {

}
