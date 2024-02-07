package castis.domain.donation.service;

import castis.domain.donation.dto.DonationDto;
import castis.domain.donation.entity.Donation;
import castis.domain.donation.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;

    public boolean save(DonationDto donationDto) {
        Donation donation = new Donation();
        donation.setDonationAccount(donationDto.getDonationAccount());
        donation.setCbankId(donationDto.getCbankId());
        donation.setCbankPass(donationDto.getCbankPass());
        donationRepository.save(donation);
        return true;
    }

    public DonationDto findByCbankId(String cbankId) {
        Optional<Donation> donation = Optional.ofNullable(donationRepository.findByCbankId(cbankId));
        if (donation.isPresent()) {
            DonationDto donationDto = new DonationDto();
            donationDto.setDonationAccount(donation.get().getDonationAccount());
            donationDto.setCbankId(donation.get().getCbankId());
            donationDto.setCbankPass(donation.get().getCbankPass());
            return donationDto;
        } else {
            return null;
        }
    }
}
