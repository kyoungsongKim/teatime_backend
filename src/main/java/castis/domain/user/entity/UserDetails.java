package castis.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.netty.handler.codec.socksx.v4.Socks4CommandRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user_details")
public class UserDetails {

    @Id
    @Column(name = "userid", length = 128, nullable = false)
    private String userId;

    @OneToOne
    @JoinColumn(name = "userid", referencedColumnName = "userid", insertable = false, updatable = false)
    @JsonBackReference
    private User user;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    @Column(name = "avatar_img", nullable = false)
    private String avatarImg;

    @Column(name = "address", length = 512, nullable = false)
    private String address;

    @Column(name = "cellphone", length = 128, nullable = false)
    private String cellphone;

    @Column(name = "email", length = 128, nullable = false)
    private String email;

    @Column(name = "dailyreportlist", length = 1024, nullable = false, columnDefinition = "VARCHAR(1024) DEFAULT ''")
    private String dailyReportList;

    @Column(name = "vacationreportlist", length = 1024, nullable = false, columnDefinition = "VARCHAR(1024) DEFAULT ''")
    private String vacationReportList;

    @Column(name = "cbank_id", length = 50)
    private String cbankId;

    @Column(name = "cbank_pwd", length = 512)
    private String cbankPwd;

    @Column(name = "cbank_account", length = 50)
    private String cbankAccount;

    @Column(name = "facebook_url", length = 512)
    private String facebookUrl;

    @Column(name = "instagram_url", length = 512)
    private String instagramUrl;

    @Column(name = "linkedin_url", length = 512)
    private String linkedinUrl;

    @Column(name = "twitter_url", length = 512)
    private String twitterUrl;

    @Column(name = "homepage_url", length = 512)
    private String homepageUrl;

    @Column(name = "education_level", length = 128)
    private String educationLevel;

    @Column(name = "skill_level", length = 512)
    private String skillLevel;

    @Column(name = "joindate")
    private LocalDate joinDate;

    @Column(name = "renewaldate")
    private LocalDate renewalDate;

    @Builder
    public UserDetails(String userId, String cellphone, String email, String dailyReportList, String vacationReportList) {
        this.userId = userId;
        this.cellphone = cellphone;
        this.email = email;
        this.dailyReportList = dailyReportList;
        this.vacationReportList = vacationReportList;
    }
}
