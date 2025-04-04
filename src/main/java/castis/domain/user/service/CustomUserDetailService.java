package castis.domain.user.service;

import castis.domain.user.CustomUserDetails;
import castis.domain.user.entity.User;
import castis.domain.user.repository.UserRepository;
import castis.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User entity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원의 정보입니다."));

        UserDetails member = new CustomUserDetails(entity.getId(), entity.getUserDetails().getEmail(), "USER");

        return member;
    }

}
