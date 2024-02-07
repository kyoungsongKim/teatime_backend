package castis.domain.donation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DonationDto {
    private Long id;
    private String donationAccount;
    private String cbankId;
    private String cbankPass;
}
