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
            value = "SELECT " +
                    "    a.id AS id, " +
                    "    u.userid AS userId, " +
                    "    ud.avatar_img AS avatarImg, " +
                    "    u.realname AS realName, " +
                    "    COUNT(CASE WHEN a.end_date >= ?2 THEN a.user_id ELSE NULL END) AS currentAgreementCount, " +
                    "    COUNT(a.user_id) AS totalAgreementCount, " +
                    "    IFNULL(SUM(CASE WHEN a.type != 'GUARANTEE' AND a.end_date >= ?2 THEN a.amount ELSE 0 END), 0) AS totalAmount, " +
                    "    IFNULL(SUM(CASE WHEN a.type = 'GUARANTEE' THEN a.amount ELSE 0 END), 0) AS guaranteeAmount " +
                    "FROM " +
                    "    users u " +
                    "LEFT JOIN " +
                    "    agreement a ON u.userid = a.user_id " +
                    "LEFT JOIN " +
                    "    user_details ud ON u.userid = ud.userid " +
                    "    AND (a.type IS NULL OR a.type NOT IN ('GUARANTEE_HISTORY', 'MANAGER_HISTORY', 'JOINED_HISTORY', 'OTHER_HISTORY')) " +
                    "WHERE u.userid = ?1 AND " +
                    "    u.teamname LIKE 'saram' " +
                    "GROUP BY " +
                    "    u.userid, " +
                    "    u.realname"
    )
    IUserAgreementInfo findUserAgreementInfo(String userId, LocalDate minDate);

    @Query(
            nativeQuery = true,
            value = "SELECT " +
                    "    a.id AS id, " +
                    "    u.userid AS userId, " +
                    "    ud.avatar_img AS avatarImg, " +
                    "    u.realname AS realName, " +
                    "    COUNT(CASE WHEN a.end_date >= ?1 THEN a.user_id ELSE NULL END) AS currentAgreementCount, " +
                    "    COUNT(a.user_id) AS totalAgreementCount, " +
                    "    IFNULL(SUM(CASE WHEN a.type != 'GUARANTEE' AND a.end_date >= ?1 THEN a.amount ELSE 0 END), 0) AS totalAmount, " +
                    "    IFNULL(SUM(CASE WHEN a.type = 'GUARANTEE' THEN a.amount ELSE 0 END), 0) AS guaranteeAmount " +
                    "FROM " +
                    "    users u " +
                    "LEFT JOIN " +
                    "    agreement a ON u.userid = a.user_id " +
                    "LEFT JOIN " +
                    "    user_details ud ON u.userid = ud.userid " +
                    "    AND (a.type IS NULL OR a.type NOT IN ('GUARANTEE_HISTORY', 'MANAGER_HISTORY', 'JOINED_HISTORY', 'OTHER_HISTORY')) " +
                    "WHERE " +
                    "    u.teamname LIKE 'saram' " +
                    "GROUP BY " +
                    "    u.userid, " +
                    "    u.realname"
    )
    List<IUserAgreementInfo> findUserAgreementInfoList(LocalDate minDate);

    @Query(
            nativeQuery = true,
            value = "SELECT " +
                    "    a.id AS id, " +
                    "    u.userid AS userId, " +
                    "    ud.avatar_img AS avatarImg, " +
                    "    u.realname AS realName, " +
                    "    COUNT(CASE WHEN a.end_date >= ?1 THEN a.user_id ELSE NULL END) AS currentAgreementCount, " +
                    "    COUNT(a.user_id) AS totalAgreementCount, " +
                    "    IFNULL(SUM(CASE WHEN a.type != 'GUARANTEE' AND a.end_date >= ?1 THEN a.amount ELSE 0 END), 0) AS totalAmount, " +
                    "    IFNULL(SUM(CASE WHEN a.type = 'GUARANTEE' THEN a.amount ELSE 0 END), 0) AS guaranteeAmount " +
                    "FROM " +
                    "    users u " +
                    "LEFT JOIN " +
                    "    agreement a ON u.userid = a.user_id " +
                    "    AND (a.type IS NULL OR a.type NOT IN ('GUARANTEE_HISTORY', 'MANAGER_HISTORY', 'JOINED_HISTORY', 'OTHER_HISTORY')) " +
                    "LEFT JOIN " +
                    "    user_details ud ON u.userid = ud.userid " +
                    "WHERE " +
                    "    u.teamname LIKE ?2 " +
                    "GROUP BY " +
                    "    u.userid, " +
                    "    u.realname"
    )
    List<IUserAgreementInfo> findUserAgreementInfoByTeamNameList(LocalDate minDate, String teamName);
}
