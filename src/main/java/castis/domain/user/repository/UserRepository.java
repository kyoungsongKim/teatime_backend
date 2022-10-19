package castis.domain.user.repository;

import castis.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    @Query(value =
            "select count(*) " +
                    "from users " +
                    "where userId = :userId ", nativeQuery = true)
    Integer countById(@Param("userId") String userId);

    @Query(value = "select * from users where userId != :userId", nativeQuery = true)
    List<User> findAllExceptId(@Param("userId") String userId);
}