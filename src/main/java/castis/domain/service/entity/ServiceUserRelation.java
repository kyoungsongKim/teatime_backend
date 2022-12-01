package castis.domain.service.entity;

import castis.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "service_user_relation")
@Entity
@NoArgsConstructor
public class ServiceUserRelation {
    @EmbeddedId
    private ServiceUserRelationPK id;

    @Column(name = "service_charge")
    private Integer serviceCharge;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}
