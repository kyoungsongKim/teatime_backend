package castis.domain.user.repository;

import castis.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByTeamName(String teamName);

    @Query(value = "SELECT u.* " +
    "FROM users u " +
    "JOIN authorities a ON u.userId = a.username " +
    "WHERE a.authority IN (:role)", nativeQuery = true)
    List<User> findAllByRoleIn(@Param("role") List<String> role);

    @Query(value = "select count(*) " +
            "from users " +
            "where userId = :userId ", nativeQuery = true)
    Integer countById(@Param("userId") String userId);

    @Query(value = "select * from users where userId != :userId order by realname", nativeQuery = true)
    List<User> findAllExceptId(@Param("userId") String userId);

    @Query(value = "select * from users join authorities on users.userId = authorities.username where authority = :role", nativeQuery = true)
    List<User> findAllByRole(@Param("role") List<String> role);
}
