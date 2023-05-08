package castis.domain.usersmsinfo.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserSMSInfoService {

    private final UserSMSInfoRepository userSMSInfoRepository;

    public UserSmsInfo getUSerSMSInfo(String userId) {
        return userSMSInfoRepository.findById(userId).orElse(null);
    }
}
