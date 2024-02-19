
package castis.domain.assistance.controller;

import castis.domain.assistance.dto.AssistanceApplyDto;
import castis.domain.assistance.dto.CreateAssistanceReviewBody;
import castis.domain.assistance.dto.UpdateAssistanceApplyStatusBody;
import castis.domain.assistance.entity.AssistanceReview;
import castis.domain.assistance.service.AssistanceApplyService;
import castis.domain.security.jwt.AuthProvider;
import castis.domain.user.CustomUserDetails;
import castis.domain.user.service.UserService;
import castis.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/assistance/apply")
public class AssistanceApplyController {
    private final UserService userService;
    private final AssistanceApplyService assistanceApplyService;

    private final AuthProvider authProvider;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<HashMap<String, List<AssistanceApplyDto>>> getAssistanceApplyList(
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        CustomUserDetails user = (CustomUserDetails) authProvider.getAuthentication(token).getPrincipal();

        List<AssistanceApplyDto> all = null;
        if (user.getRoles().contains(UserRole.ROLE_ADMIN.getValue())) {
            all = assistanceApplyService.getAssistanceApplyList();
        }
        List<AssistanceApplyDto> personal = assistanceApplyService.getAssistanceApplyListByApplierId(user.getUserId());

        HashMap<String, List<AssistanceApplyDto>> result = new HashMap<>();
        if (all != null) {
            result.put("all", all);
        }
        result.put("my", personal);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{assistanceApplyId}", method = RequestMethod.GET)
    public ResponseEntity<AssistanceApplyDto> getAssistanceApply(HttpServletRequest request,
            @PathVariable Integer assistanceApplyId) {
        assistanceApplyService.getAssistanceApply(assistanceApplyId);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/{assistanceApplyId}/status", method = RequestMethod.PATCH)
    @Transactional
    public ResponseEntity<AssistanceApplyDto> receiveCompleteAssistance(HttpServletRequest request,
            @PathVariable Integer assistanceApplyId, @RequestBody UpdateAssistanceApplyStatusBody body) {
        // String token = request.getHeader("Authorization");
        // CustomUserDetails user = (CustomUserDetails)
        // authProvider.getAuthentication(token).getPrincipal();
        assistanceApplyService.changeAssistanceApplyStatus(assistanceApplyId, body.getStatus());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/{assistanceApplyId}/receive", method = RequestMethod.PATCH)
    @Transactional
    public ResponseEntity<AssistanceApplyDto> receiveCompleteAssistance(HttpServletRequest request,
            @PathVariable Integer assistanceApplyId) {
        String token = request.getHeader("Authorization");
        CustomUserDetails user = (CustomUserDetails) authProvider.getAuthentication(token).getPrincipal();
        assistanceApplyService.receiveAssistanceApply(assistanceApplyId, user.getUserId());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/{assistanceApplyId}/review", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<AssistanceApplyDto> reviewCompleteAssistance(HttpServletRequest request,
            @RequestBody CreateAssistanceReviewBody body,
            @PathVariable Integer assistanceApplyId) {
        String token = request.getHeader("Authorization");
        CustomUserDetails user = (CustomUserDetails) authProvider.getAuthentication(token).getPrincipal();
        AssistanceReview review = new AssistanceReview();
        review.setContent(body.getContent());
        review.setRating(body.getRating());
        review.setReviewer(userService.getUser(user.getUserId()));
        try {
            assistanceApplyService.reviewAssistanceApply(assistanceApplyId, review);
            return new ResponseEntity<>(null, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}
