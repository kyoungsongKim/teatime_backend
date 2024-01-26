package castis.domain.authorities.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import castis.enums.UserRole;

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
    @Enumerated(value = EnumType.STRING)
    private UserRole authority;

    public Authorities(String userName, UserRole role) {
        this.userName = userName;
        this.authority = role;
    }
}