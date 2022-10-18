package castis.domain.team.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@Table(name = "team")
public class Team {
    @Id
    @Size(max = 128)
    @Column(name = "teamname", nullable = false, length = 128)
    private String teamName;

    @NotNull
    @Lob
    @Column(name = "teamdescription", nullable = false)
    private String teamDescription;
}