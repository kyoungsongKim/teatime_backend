package castis.domain.assistance.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AppliedAssistanceStatus {
    WAITING,
    IN_PROGRESS,
    COMPLETED;

    @JsonCreator
    public static AppliedAssistanceStatus from(String s) {
        return AppliedAssistanceStatus.valueOf(s.toUpperCase());
    }
}
