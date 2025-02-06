package castis.domain.usernavigationhistory.service;

import castis.domain.usernavigationhistory.dto.UserNavigationHistoryDto;
import castis.domain.usernavigationhistory.entity.UserNavigationHistory;
import castis.domain.usernavigationhistory.repository.UserNavigationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNavigationHistoryService {
    private final UserNavigationHistoryRepository userNavigationHistoryRepository;

    public List<UserNavigationHistory> getAllNavigationHistory() {
        return userNavigationHistoryRepository.findAll();
    }

    public UserNavigationHistory getById(Long id) {
        return userNavigationHistoryRepository.findById(id).orElse(null);
    }

    public List<UserNavigationHistory> getByUserId(String userId) {
        return userNavigationHistoryRepository.findByUserId(userId);
    }

    public List<UserNavigationHistory> getByMenuName(String menuName) {
        return userNavigationHistoryRepository.findByMenuName(menuName);
    }

    public UserNavigationHistory saveNavigationHistory(String userId, String ipAddress, UserNavigationHistoryDto historyDto) {
        UserNavigationHistory userNavigationHistory = new UserNavigationHistory();
        userNavigationHistory.setUserId(userId);
        userNavigationHistory.setMenuName(historyDto.getMenuName());
        userNavigationHistory.setUserAgent(historyDto.getUserAgent());
        userNavigationHistory.setIpAddress(ipAddress);
        userNavigationHistory.setAccessedAt(LocalDateTime.now());
        return userNavigationHistoryRepository.save(userNavigationHistory);
    }

    public void deleteNavigationHistory(Long id) {
        userNavigationHistoryRepository.deleteById(id);
    }
}
