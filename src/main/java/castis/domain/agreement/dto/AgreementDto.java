package castis.domain.agreement.dto;

import castis.domain.agreement.entity.Agreement;
import castis.domain.agreement.util.AgreementType;
import castis.domain.filesystem.dto.FileMetaDto;
import castis.domain.user.dto.UserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class AgreementDto {
    private Long id;

    private UserDto user;

    private FileMetaDto file;
    private Integer amount;

    private AgreementType type;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDate createDate;
    private LocalDate updatedDate;

    @Builder
    public AgreementDto(UserDto user, FileMetaDto file, int amount, AgreementType type, LocalDate startDate, LocalDate endDate, LocalDate createDate, LocalDate updatedDate) {
        this.user = user;
        this.file = file;
        this.amount = amount;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createDate = createDate;
        this.updatedDate = updatedDate;
    }

    public AgreementDto(Agreement agreement) {
        this.id = agreement.getId();
        this.user = new UserDto(agreement.getUser());
        this.file = new FileMetaDto(agreement.getFile());
        this.amount = agreement.getAmount();
        this.type = agreement.getType();
        this.startDate = agreement.getStartDate();
        this.endDate = agreement.getEndDate();
        this.createDate = agreement.getCreateDate();
        this.updatedDate = agreement.getUpdatedDate();
    }
}
