package castis.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardreplyRepository extends JpaRepository<Boardreply, Integer> {
    Optional<List<Boardreply>> findByBrdnoOrderByReorder(int brdno);
}