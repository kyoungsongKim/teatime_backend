package castis.domain.authorities.entity;

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
@Table(name = "authorities")
public class Authorities {
    @Id
    @Column(name = "username", nullable = false, length = 50)
    private String userName;

    @Column(name = "authority", nullable = false, length = 50)
    private String authority;

    public Authorities(String userName, String role) {
        this.userName = userName;
        this.authority = role;
    }
}