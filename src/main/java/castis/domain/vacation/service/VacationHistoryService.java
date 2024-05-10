package castis.domain.vacation.service;

import castis.domain.vacation.dto.VacationHistoryDto;
import castis.domain.vacation.dto.VacationInfoDto;
import castis.domain.vacation.entity.IVacationInfo;
import castis.domain.vacation.entity.VacationHistory;
import castis.domain.vacation.repository.VacationHistoryRepository;
import castis.util.vacation.MaximumVacationCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class VacationHistoryService {

    private final VacationHistoryRepository vacationHistoryRepository;
    private final MaximumVacationCalculator maximumVacationCalculator;

    public VacationHistoryDto getById(long id) {
        VacationHistory foundEntity = vacationHistoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return new VacationHistoryDto(foundEntity);
    }

    public void create(VacationHistory vacationHistory) {
        vacationHistoryRepository.save(vacationHistory);
    }

    public void delete(Long vacationHistoryId) {
        vacationHistoryRepository.deleteById(vacationHistoryId);
    }

    public List<VacationHistoryDto> getVacationHistoryList() {
        List<VacationHistoryDto> result = vacationHistoryRepository.findAll().stream().map(VacationHistoryDto::new)
                .collect(Collectors.toList());

        return result;
    }

    public List<VacationHistoryDto> getVacationHistoryList(LocalDateTime startDate,
            LocalDateTime endDate, boolean includeAmount) {

        List<VacationHistoryDto> result = vacationHistoryRepository
                .findAllByBetweenDateRange(startDate, endDate, includeAmount).stream()
                .map(VacationHistoryDto::new)
                .collect(Collectors.toList());

        return result;
    }

    public List<VacationHistoryDto> getVacationHistoryList(String userId, LocalDateTime startDate,
            LocalDateTime endDate, boolean includeAmount) {

        List<VacationHistoryDto> result = vacationHistoryRepository
                .findAllByUserIdAndBetweenDateRange(userId, startDate, endDate, includeAmount).stream()
                .map(VacationHistoryDto::new)
                .collect(Collectors.toList());

        return result;
    }

    private VacationInfoDto calculateTotalVacationAmount(VacationInfoDto vacationInfo) {
        if (vacationInfo.getRenewalDate() == null)
            return vacationInfo;
        float totalVacation = maximumVacationCalculator.getMaximumVacation(vacationInfo.getRenewalDate());
        Float usedVacation = vacationInfo.getUsed();
        usedVacation = usedVacation == null ? 0 : usedVacation;
        float remainVacation = (totalVacation - usedVacation);
        vacationInfo.setLeft(remainVacation);
        vacationInfo.setUsed(usedVacation);
        vacationInfo.setTotal(totalVacation);
        return vacationInfo;
    }

    private VacationInfoDto calculateTotalVacationAmount(VacationInfoDto vacationInfo, LocalDate targetDate) {
        if (vacationInfo.getRenewalDate() == null)
            return vacationInfo;
        byte years = (byte) (Period.between(vacationInfo.getRenewalDate(), targetDate).getYears() + 1);
        float totalVacation = maximumVacationCalculator.getMaximumVacation(years);
        Float usedVacation = vacationInfo.getUsed();
        usedVacation = usedVacation == null ? 0 : usedVacation;
        float remainVacation = (totalVacation - usedVacation);
        vacationInfo.setLeft(remainVacation);
        vacationInfo.setUsed(usedVacation);
        vacationInfo.setTotal(totalVacation);
        return vacationInfo;
    }

    public VacationInfoDto getVacationInfo(String userId, LocalDateTime targetDate, boolean includeAmount) {

        Optional<IVacationInfo> found = vacationHistoryRepository.findVacationInfo(userId, targetDate, includeAmount);
        if (found.isPresent()) {
            return calculateTotalVacationAmount(new VacationInfoDto(found.get()), targetDate.toLocalDate());
        } else {
            VacationInfoDto result = new VacationInfoDto();
            result.setUserId(userId);
            return result;

        }
    }

    public List<VacationInfoDto> getAllVacationInfo(LocalDateTime targetDate, boolean includeAmount) {

        List<VacationInfoDto> list = vacationHistoryRepository.findAllVacationInfo(targetDate, includeAmount).stream()
                .map(iVacationInfo -> {
                    return calculateTotalVacationAmount(new VacationInfoDto(iVacationInfo), targetDate.toLocalDate());
                })
                .collect(Collectors.toList());
        return list;
    }

    public void update(VacationHistory vh) {
        vacationHistoryRepository.save(vh);
    }
}
