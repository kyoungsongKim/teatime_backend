package castis.domain.faqs.controller;

import castis.domain.faqs.dto.FaqsDto;
import castis.domain.faqs.entity.Faqs;
import castis.domain.faqs.service.FaqsService;
import castis.domain.security.jwt.AuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/faqs")
public class FaqsController {

    private final FaqsService faqService;
    private final AuthProvider authProvider;

    @GetMapping("")
    public List<Faqs> getFaqs() {
        return faqService.getFaqs();
    }

    @PostMapping(value = "")
    @Transactional
    public ResponseEntity<Void> postFaqs(HttpServletRequest httpServletRequest, @RequestBody FaqsDto faqsDto) {
        try {
            boolean isAdmin = authProvider.isAdmin(httpServletRequest);
            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            faqService.postFaqs(faqsDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping(value = "/{faqsId}")
    public ResponseEntity<Void> deleteFaqs(HttpServletRequest httpServletRequest, @PathVariable int faqsId) {
        try {
            boolean isAdmin = authProvider.isAdmin(httpServletRequest);
            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            faqService.deleteFaqs(faqsId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
