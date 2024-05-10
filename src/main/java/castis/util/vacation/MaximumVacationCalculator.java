package castis.util.vacation;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaximumVacationCalculator {

    private final MaximumVacationConfig config;

    public float getMaximumVacation(byte continuousYears) {
        Float foundValue = null;
        while (foundValue == null) {
            foundValue = config.getMaximumVacationRule().get(continuousYears--);
        }
        return foundValue;
    }

    public float getMaximumVacation(LocalDate renewalDate) {
        byte continuousYears = (byte) (LocalDate.now().getYear() - renewalDate.getYear());
        return getMaximumVacation(continuousYears);
    }
}