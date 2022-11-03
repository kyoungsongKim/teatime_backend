package castis.domain.board.repository;

import castis.domain.board.entity.Board;
import castis.domain.board.entity.Boardreply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardreplyRepository extends JpaRepository<Boardreply, Integer> {
    Optional<List<Boardreply>> findByBrdnoAndRedeleteflagOrderByReorder(Board brdno, char flag);
    Optional<Boardreply> findTop1ByBrdnoOrderByReorderDesc(Board brdno);
    Optional<Boardreply> findByBrdnoAndIdOrderByReorderDesc(Board brdno, int id);

    Optional<Boardreply> findTop1ByBrdnoAndRedepthAndReorderGreaterThanOrderByReorder(Board brdno, int depth, int order);
    Optional<List<Boardreply>> findByBrdnoAndReorderGreaterThanEqual(Board brdno, int order);

    Optional<List<Boardreply>> findByBrdno(Board brdno);
    Optional<Boardreply> findById(int id);

}