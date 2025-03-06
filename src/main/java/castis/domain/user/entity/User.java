package castis.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

    @Column(name = "description")
    private String description;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private UserDetails userDetails;

    @Builder
    public User(String id, String userName, String realName, String password,
            String teamName, String position) {
        this.id = id;
        this.userName = userName;
        this.realName = realName;
        this.password = password;
        this.teamName = teamName;
        this.position = position;
    }
}
