package castis.domain.usernavigationhistory.controller;

import castis.domain.security.jwt.AuthProvider;
import castis.domain.usernavigationhistory.dto.UserNavigationHistoryDto;
import castis.domain.usernavigationhistory.entity.UserNavigationHistory;
import castis.domain.usernavigationhistory.service.UserNavigationHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/navigation-history")
public class UserNavigationHistoryController {
    private final UserNavigationHistoryService userNavigationHistoryService;
    private final AuthProvider authProvider;

    @GetMapping
    public ResponseEntity<List<UserNavigationHistory>> getAllNavigationHistory() {
        return ResponseEntity.ok(userNavigationHistoryService.getAllNavigationHistory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserNavigationHistory> getNavigationHistoryById(@PathVariable Long id) {
        return ResponseEntity.ok(userNavigationHistoryService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserNavigationHistory>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(userNavigationHistoryService.getByUserId(userId));
    }

    @GetMapping("/menu/{menuName}")
    public ResponseEntity<List<UserNavigationHistory>> getByMenuName(@PathVariable String menuName) {
        return ResponseEntity.ok(userNavigationHistoryService.getByMenuName(menuName));
    }

    @PostMapping
    public ResponseEntity<UserNavigationHistory> createNavigationHistory(HttpServletRequest httpServletRequest, @RequestBody UserNavigationHistoryDto historyDto) {
        try {
            String userId = authProvider.getUserIdFromRequest(httpServletRequest);
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            String ipAddress = getClientIpAddress(httpServletRequest);
            return ResponseEntity.ok(userNavigationHistoryService.saveNavigationHistory(userId, ipAddress, historyDto));
        } catch (Exception e) {
            log.error("Error occurred while saving navigation history: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNavigationHistory(@PathVariable Long id) {
        userNavigationHistoryService.deleteNavigationHistory(id);
        return ResponseEntity.noContent().build();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // X-Forwarded-For 헤더에는 여러 개의 IP가 올 수 있으므로 첫 번째 IP만 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
