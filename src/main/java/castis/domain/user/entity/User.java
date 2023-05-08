package castis.domain.user.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @Column(name = "userid", nullable = false, length = 128)
    private String id;

    @Column(name = "username", nullable = false, length = 50)
    private String userName;

    @Column(name = "realname", nullable = false, length = 50)
    private String realName;

    @Column(name = "password", nullable = false, length = 256)
    private String password;

    @Column(name = "teamname", nullable = false, length = 128)
    private String teamName;

    @Column(name = "position", nullable = false, length = 128)
    private String position;

    @Column(name = "cellphone", nullable = false, length = 128)
    private String cellphone;

    @Column(name = "email", nullable = false, length = 128)
    private String email;

    @Column(name = "dailyreportlist", nullable = false, length = 1024)
    private String dailyReportList;

    @Column(name = "vacationreportlist", nullable = false, length = 1024)
    private String vacationReportList;

    @Column(name = "cbank_id", length = 50)
    private String cbankId;

    @Column(name = "cbank_account", length = 50)
    private String cbankAccount;

    @Builder
    public User(String id, String userName, String realName, String password,
                String teamName, String position,
                String cellphone, String email, String dailyReportList, String vacationReportList) {
        this.id = id;
        this.userName = userName;
        this.realName = realName;
        this.password = password;
        this.teamName = teamName;
        this.position = position;
        this.cellphone = cellphone;
        this.email = email;
        this.dailyReportList = dailyReportList;
        this.vacationReportList = vacationReportList;
    }
}