package castis.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    //Optional<List<Board>> findByBoardGroupOrderByBoardNumDescWithPagination(Long boardGroup, Pageable pageable);
    Optional<List<Board>> findByBoardGroupAndBrddeleteflagOrderByBoardNumDesc(long boardGroup, char flag, Pageable pageable);

    Optional<Board> findByBoardNumAndBrddeleteflag(long boardNum, char flag);
    long countBoardByBoardGroupAndBrddeleteflag(long boardGroup, char flag);


}