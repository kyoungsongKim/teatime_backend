package castis.domain.service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceUserRelationPK implements Serializable {
    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "user_id")
    private String userId;
}
