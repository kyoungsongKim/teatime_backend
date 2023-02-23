package castis.domain.board.repository;

import castis.domain.board.entity.Boardfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BoardfileRepository extends JpaRepository<Boardfile, Integer> {
    Optional<List<Boardfile>> findByBrdno(int brdno);
}