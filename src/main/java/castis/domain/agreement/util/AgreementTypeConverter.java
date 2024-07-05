package castis.domain.agreement.util;

import javax.persistence.AttributeConverter;

public class AgreementTypeConverter implements AttributeConverter<AgreementType, String> {

    @Override
    public String convertToDatabaseColumn(AgreementType attribute) {
        return attribute.getValue();
    }

    @Override
    public AgreementType convertToEntityAttribute(String dbData) {
        return AgreementType.valueOf(dbData);
    }
}
