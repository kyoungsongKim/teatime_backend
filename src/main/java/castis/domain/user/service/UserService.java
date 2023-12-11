package castis.domain.user.service;

import castis.domain.user.dto.PasswordDto;
import castis.domain.user.dto.UserDto;
import castis.domain.user.dto.UserUpdateDto;
import castis.domain.user.entity.User;
import castis.domain.user.repository.UserRepository;
import castis.exception.custom.ForbiddenException;
import castis.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public User getUser(String userId) throws EntityNotFoundException {
        return userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
    }

    public UserDto getUserDtoById(String id) {
        Optional<User> user = findById(id);
        if (user.isPresent()) {
            return new UserDto(user.get());
        } else {
            throw new UserNotFoundException("User Not Found");
        }
    }

    public List<User> getUserList() {
        return userRepository.findAll();
    }

    public List<UserDto> getUserDtoList() {
        return userRepository.findAll().stream().map(UserDto::new).collect(Collectors.toList());
    }

    public Boolean updatePassword(PasswordDto passwordDto) {
        // 회원 엔티티 조회시작
        User user = userRepository.findById(passwordDto.getId())
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        // 이전 비밀번호와 맞는지 체크
        if (!passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword()))
            throw new ForbiddenException("Passwords do not match");

        // 비밀번호 암호화처리
        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));

        // 데이터 등록(저장)
        userRepository.save(user);

        return true;
    }

    public Boolean updateInfo(UserUpdateDto updateDto) {
        // 회원 엔티티 조회시작
        User user = userRepository.findById(updateDto.getId())
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        user.setUserName(updateDto.getName());
        user.setTeamName(updateDto.getTeam());
        user.setPosition(updateDto.getPosition());
        user.setCellphone(updateDto.getPhone());
        user.setEmail(updateDto.getEmail());
        user.setDailyReportList(updateDto.getReportEmail());
        user.setVacationReportList(updateDto.getVacationReportList());

        // 데이터 등록(저장)
        userRepository.save(user);

        return true;
    }

    public List<UserDto> findAllUserDtoExceptId(String id) {
        List<User> userList = userRepository.findAllExceptId(id);
        List<UserDto> result = new ArrayList<>();

        userList.forEach(i -> {
            result.add(new UserDto(i));
        });

        return result;
    }
}
