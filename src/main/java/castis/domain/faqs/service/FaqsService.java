package castis.domain.faqs.service;

import castis.domain.faqs.dto.FaqsDto;
import castis.domain.faqs.entity.Faqs;
import castis.domain.faqs.repository.FaqsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqsService {
    private final FaqsRepository faqsRepository;

    public List<Faqs> getFaqs() {
        return faqsRepository.findAll();
    }

    public void postFaqs(FaqsDto faqsDto) {
        Faqs faqs = new Faqs();
        if(faqsDto.getId() != null) faqs.setId(faqsDto.getId());
        faqs.setName(faqsDto.getName());
        faqs.setDescription(faqsDto.getDescription());
        faqsRepository.save(faqs);
    }

    public void deleteFaqs(int faqsId) {
        faqsRepository.deleteById(faqsId);
    }
}
