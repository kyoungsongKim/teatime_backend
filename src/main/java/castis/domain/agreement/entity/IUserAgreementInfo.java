package castis.domain.agreement.entity;

public interface IUserAgreementInfo {
    Integer getId();

    String getUserId();

    String getRealName();

    String getAvatarImg();

    Integer getGuaranteeAmount();

    Integer getTotalAmount();

    Integer getCurrentAgreementCount();

    Integer getTotalAgreementCount();
}
