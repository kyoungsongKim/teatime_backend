package castis.domain.agreement.util;

import lombok.Getter;

@Getter
public enum AgreementType {
    GUARANTEE("GUARANTEE"),
    MANAGER("MANAGER"),
    JOINED("JOINED"),
    OTHER("OTHER");

    final String value;

    AgreementType(String value) {
        this.value = value;
    }

}
