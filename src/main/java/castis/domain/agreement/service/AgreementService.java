package castis.domain.agreement.service;

import castis.domain.agreement.dto.AgreementDto;
import castis.domain.agreement.entity.Agreement;
import castis.domain.agreement.entity.IUserAgreementInfo;
import castis.domain.agreement.repository.AgreementRepository;
import castis.domain.agreement.util.AgreementType;
import castis.domain.filesystem.entity.FileMeta;
import castis.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgreementService {

    private final AgreementRepository agreementRepository;

    private static final Set<AgreementType> GENERAL_TYPES = EnumSet.of(
            AgreementType.GUARANTEE,
            AgreementType.MANAGER,
            AgreementType.JOINED,
            AgreementType.OTHER
    );

    private static final Set<AgreementType> HISTORY_TYPES = EnumSet.of(
            AgreementType.GUARANTEE,
            AgreementType.GUARANTEE_HISTORY
    );

    public List<AgreementDto> getAgreementListByUser(String userId) {
        return agreementRepository.findAllByUserId(userId)
                .stream()
                .filter(agreement -> GENERAL_TYPES.contains(agreement.getType()))
                .map(AgreementDto::new)
                .collect(Collectors.toList());
    }

    public List<AgreementDto> getAgreementHistoryListByUser(String userId) {
        return agreementRepository.findAllByUserId(userId)
                .stream()
                .filter(greement -> !HISTORY_TYPES.contains(greement.getType()))
                .map(AgreementDto::new)
                .collect(Collectors.toList());
    }

    public List<IUserAgreementInfo> getUserAgreementInfoList() {
        return agreementRepository.findUserAgreementInfoList(LocalDateTime.now().toLocalDate());
    }

    public IUserAgreementInfo getUserAgreementInfo(String userId) {
        return agreementRepository.findUserAgreementInfo(userId, LocalDateTime.now().toLocalDate());
    }

    @Transactional
    public void createAgreement(AgreementDto agreementDto) {
        FileMeta fileMeta = new FileMeta();
        fileMeta.setId(agreementDto.getFile().getId());
        User user = new User();
        user.setId(agreementDto.getUser().getId());
        Agreement agreement = Agreement.builder()
                .user(user)
                .file(fileMeta)
                .type(agreementDto.getType())
                .amount(agreementDto.getAmount())
                .startDate(agreementDto.getStartDate())
                .endDate(agreementDto.getEndDate())
                .build();
        agreementRepository.save(agreement);
    }

    @Transactional
    public void deleteAgreement(Long agreementId) {
        agreementRepository.deleteById(agreementId);
    }

    private boolean isGeneralType(String type) {
        return type.equals("GUARANTEE") ||
                type.equals("MANAGER") ||
                type.equals("JOINED") ||
                type.equals("OTHER");
    }

    private boolean isHistoryType(String type) {
        return type.equals("GUARANTEE_HISTORY") ||
                type.equals("MANAGER_HISTORY") ||
                type.equals("JOINED_HISTORY");
    }
}
