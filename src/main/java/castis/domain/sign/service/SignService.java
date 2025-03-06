package castis.domain.sign.service;

import castis.domain.authorities.entity.Authorities;
import castis.domain.authorities.repository.AuthoritiesRepository;
import castis.domain.sign.dto.AuthenticationDto;
import castis.domain.sign.dto.JoinDto;
import castis.domain.sign.dto.LoginDto;
import castis.domain.user.entity.User;
import castis.domain.user.repository.UserDetailsRepository;
import castis.domain.user.repository.UserRepository;
import castis.enums.UserRole;
import castis.exception.custom.DuplicatedException;
import castis.exception.custom.ForbiddenException;
import castis.exception.custom.UserNotFoundException;
import castis.util.validation.Empty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("signService")
@Slf4j
@RequiredArgsConstructor
public class SignService {

    private final UserRepository userRepository;
    private final AuthoritiesRepository authoritiesRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserDetailsRepository userDetailsRepository;

    @Transactional
    public Boolean regMember(JoinDto joinDto) {

        // 아이디 중복체크
        if (!Empty.validation(userRepository.countById(joinDto.getId())))
            throw new DuplicatedException("Duplicated ID");

        // 비밀번호 암호화처리
        joinDto.setPassword(passwordEncoder.encode(joinDto.getPassword()));

        // 데이터 등록(저장)
        userRepository.save(joinDto.toEntity());
        userDetailsRepository.save(joinDto.toEntityForDetail());
        authoritiesRepository.save(new Authorities(joinDto.getId(), UserRole.ROLE_USER_BASIC));

        return true;
    }

    public AuthenticationDto loginMember(LoginDto loginDto) {

        // dto -> entity
        User loginEntity = loginDto.toEntity();
        // 회원 엔티티 객체 생성 및 조회시작
        User user = userRepository.findById(loginEntity.getId())
                .orElseThrow(() -> {
                    log.error("there is some error at sing-in loginEntity.getId():{}", loginEntity.getId());
                    return new UserNotFoundException("User Not Found");
                });

        if (!passwordEncoder.matches(loginEntity.getPassword(), user.getPassword()))
            throw new ForbiddenException("Passwords do not match");

        // 회원정보를 인증클래스 객체(authentication)로 매핑

        return AuthenticationDto.builder()
                .userId(user.getId())
                .realName(user.getRealName())
                .teamName(user.getTeamName())
                .position(user.getPosition())
                .cellphone(user.getUserDetails().getCellphone())
                .email(user.getUserDetails().getEmail())
                .dailyReportList(user.getUserDetails().getDailyReportList())
                .vacationReportList(user.getUserDetails().getVacationReportList())
                .build();
    }

    public UserRole checkIsAdmin(String userId) {
        Authorities authorities = authoritiesRepository.findByUserName(userId);
        return authorities.getAuthority();
    }
}
