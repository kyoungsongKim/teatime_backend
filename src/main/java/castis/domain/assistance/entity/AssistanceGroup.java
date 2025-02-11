package castis.domain.assistance.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Table(name = "assistance_group")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class AssistanceGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private Integer order;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assistance> services;

    public AssistanceGroup(String name, Integer order) {
        this.name = name;
        this.order = order;
    }

    public AssistanceGroup(String name) {
        this.name = name;
    }
}

