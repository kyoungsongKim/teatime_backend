package castis.domain.usersmsinfo.entity;

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
@Table(name = "user_sms_info")
public class UserSmsInfo {
    @Id
    @Column(name = "userid", nullable = false, length = 128)
    private String id;

    @Column(name = "realname", nullable = false, length = 50)
    private String realName;

    @Column(name = "sendday")
    private int sendday;

    @Column(name = "issendmsg")
    private boolean sendmsg;
}