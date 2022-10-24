package castis;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BoardfileRepository extends JpaRepository<Boardfile, Integer> {
    Optional<Boardfile> findByBrdno(int brdno);
}