package castis.domain.board.repository;

import castis.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    //Optional<List<Board>> findByBoardGroupOrderByBoardNumDescWithPagination(Long boardGroup, Pageable pageable);
    Optional<List<Board>> findByBoardGroupAndBrddeleteflagOrderByBoardNumDesc(long boardGroup, char flag, Pageable pageable);
    Optional<List<Board>> findByBoardGroupAndAgreementUserIdAndBrddeleteflagOrderByBoardNumDesc(long boardGroup, String agreementUserId, char flag, Pageable pageable);

    Optional<Board> findByBoardNumAndBrddeleteflag(long boardNum, char flag);
    Optional<Board> findByBoardNum(long boardNum);
    long countBoardByBoardGroupAndBrddeleteflag(long boardGroup, char flag);
    long countBoardByBoardGroupAndAgreementUserIdAndBrddeleteflag(long boardGroup, String agreementUserId, char flag);


}