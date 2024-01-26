package castis.domain.authorities.repository;

import castis.domain.authorities.entity.Authorities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthoritiesRepository extends JpaRepository<Authorities, String> {
    Authorities findByUserName(String userName);
}