package castis.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public UserDto getUserDtoById(String id) {
        Optional<User> user = findById(id);
        if (user.isPresent()) {
            return new UserDto(user.get());
        } else {
            return null;
        }
    }
}
