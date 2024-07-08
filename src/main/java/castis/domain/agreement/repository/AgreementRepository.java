package castis.domain.agreement.repository;

import castis.domain.agreement.entity.Agreement;
import castis.domain.agreement.entity.IUserAgreementInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgreementRepository
        extends JpaRepository<Agreement, Long>, JpaSpecificationExecutor<Agreement> {

    List<Agreement> findAllByUserId(String userId);

    @Query(
            nativeQuery = true,
            value = "SELECT u.userid as userId, u.realname as realName, IFNULL(SUM(a.amount),0) as totalAmount, (SELECT a2.amount FROM agreement a2 WHERE a2.user_id = ?1 AND a2.type = 'GUARANTEE') as guaranteeAmount FROM agreement a RIGHT OUTER JOIN users u ON a.user_id = u.userid AND a.type != 'GUARANTEE' AND a.end_date >= ?2 WHERE u.userid = ?1 AND u.teamname LIKE '사람' GROUP BY  u.userid, u.realname"
    )
    IUserAgreementInfo findUserAgreementInfo(String userId, LocalDate minDate);

    @Query(
            nativeQuery = true,
            value = "SELECT userid as userId, u.realname as realName, IFNULL(SUM(a.amount),0) as totalAmount, (SELECT a2.amount FROM agreement a2 WHERE a2.user_id = userid AND a2.type = 'GUARANTEE' LIMIT 1) as guaranteeAmount FROM agreement a RIGHT OUTER JOIN users u ON a.user_id = userid AND a.type != 'GUARANTEE' AND a.end_date >= ?1 WHERE u.teamname LIKE '사람' GROUP BY userid, u.realname"
    )
    List<IUserAgreementInfo> findUserAgreementInfoList(LocalDate minDate);
}
