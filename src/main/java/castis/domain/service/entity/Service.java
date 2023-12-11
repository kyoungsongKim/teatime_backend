package castis.domain.service.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Table(name = "service")
@Entity
@NoArgsConstructor
public class Service {

    @Id
    private Integer id;

    @Column
    private String name;

    @Column
    private String description;
}
