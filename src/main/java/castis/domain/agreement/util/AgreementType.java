package castis.domain.agreement.util;

import lombok.Getter;

@Getter
public enum AgreementType {
    GUARANTEE("GUARANTEE"),
    MANAGER("MANAGER"),
    JOINED("JOINED"),
    OTHER("OTHER"),
    GUARANTEE_HISTORY("GUARANTEE_HISTORY"),
    MANAGER_HISTORY("MANAGER_HISTORY"),
    JOINED_HISTORY("JOINED_HISTORY"),
    OTHER_HISTORY("OTHER_HISTORY");

    final String value;

    AgreementType(String value) {
        this.value = value;
    }
}
