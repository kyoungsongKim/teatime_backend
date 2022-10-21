package castis.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardreplyRepository extends JpaRepository<Boardreply, Integer> {
    Optional<List<Boardreply>> findByBrdnoAndRedeleteflagOrderByReorder(int brdno, char flag);
    Optional<Boardreply> findTop1ByBrdnoOrderByReorderDesc(int brdno);
    Optional<Boardreply> findByBrdnoAndIdOrderByReorderDesc(int brdno, int id);

    Optional<Boardreply> findTop1ByBrdnoAndRedepthAndReorderGreaterThanOrderByReorder(int brdno, int depth, int order);
    Optional<List<Boardreply>> findByBrdnoAndReorderGreaterThanEqual(int brdno, int order);

    Optional<Boardreply> findById(int id);
}