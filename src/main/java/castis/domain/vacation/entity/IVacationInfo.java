package castis.domain.vacation.entity;

import java.time.LocalDate;

public interface IVacationInfo {
    String getUserId();

    Float getLeft();

    Float getUsed();

    Float getTotal();

    LocalDate getRenewalDate();
}
