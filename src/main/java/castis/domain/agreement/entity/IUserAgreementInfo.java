package castis.domain.agreement.entity;

public interface IUserAgreementInfo {
    Integer getId();

    String getUserId();

    String getRealName();

    Integer getGuaranteeAmount();

    Integer getTotalAmount();

    Integer getCurrentAgreementCount();

    Integer getTotalAgreementCount();
}
