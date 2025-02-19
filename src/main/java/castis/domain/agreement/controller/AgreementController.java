package castis.domain.agreement.controller;

import castis.domain.agreement.dto.AgreementDto;
import castis.domain.agreement.dto.CreateAgreementRequestBody;
import castis.domain.agreement.entity.IUserAgreementInfo;
import castis.domain.agreement.service.AgreementService;
import castis.domain.filesystem.dto.FileMetaDto;
import castis.domain.filesystem.service.FileSystemService;
import castis.domain.security.jwt.AuthProvider;
import castis.domain.user.dto.UserDto;
import castis.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/agreement")
public class AgreementController {

    private final AgreementService agreementService;
    private final AuthProvider authProvider;
    private final FileSystemService fileSystemService;
    private final UserService userService;

    @GetMapping(value = "/{userId}")
    public ResponseEntity<List<AgreementDto>> getAgreementList(
            HttpServletRequest httpServletRequest,
            @PathVariable String userId
    ) {
        boolean isOwn = authProvider.isOwn(httpServletRequest, userId);
        if (!isOwn) {
            boolean isAdmin = authProvider.isAdmin(httpServletRequest);
            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }

        List<AgreementDto> result = agreementService.getAgreementListByUser(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/detail/{userId}")
    public ResponseEntity<List<AgreementDto>> getAgreementDetailList(
            HttpServletRequest httpServletRequest,
            @PathVariable String userId
    ) {
        boolean isOwn = authProvider.isOwn(httpServletRequest, userId);
        if (!isOwn) {
            boolean isAdmin = authProvider.isAdmin(httpServletRequest);
            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }

        List<AgreementDto> result = agreementService.getAgreementHistoryListByUser(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/info")
    public ResponseEntity<List<IUserAgreementInfo>> getUserAgreementInfoList(HttpServletRequest httpServletRequest) {
        boolean isAdmin = authProvider.isAdmin(httpServletRequest);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<IUserAgreementInfo> result = agreementService.getUserAgreementInfoList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/info/{userId}")
    public ResponseEntity<IUserAgreementInfo> getUserAgreementInfo(HttpServletRequest httpServletRequest, @PathVariable String userId) {
        boolean isOwn = authProvider.isOwn(httpServletRequest, userId);
        if (!isOwn) {
            boolean isAdmin = authProvider.isAdmin(httpServletRequest);
            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }

        IUserAgreementInfo result = agreementService.getUserAgreementInfo(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "")
    @Transactional
    public ResponseEntity<Void> createAgreement(
            HttpServletRequest httpServletRequest,
            @RequestPart MultipartFile file,
            @RequestPart CreateAgreementRequestBody body
    ) {
        boolean isAdmin = authProvider.isAdmin(httpServletRequest);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        UserDto foundUser = userService.getUserDtoById(body.getUserId());
        if (foundUser == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }
        FileMetaDto savedFile;
        try {
            savedFile = new FileMetaDto(fileSystemService.saveFile(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        agreementService.createAgreement(AgreementDto.builder()
                .type(body.getType())
                .amount(body.getAmount())
                .startDate(body.getStartDate())
                .endDate(body.getEndDate())
                .user(foundUser)
                .file(savedFile)
                .build());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{agreementId}")
    public ResponseEntity<Void> deleteAgreement(HttpServletRequest httpServletRequest, @PathVariable long agreementId) {
        boolean isAdmin = authProvider.isAdmin(httpServletRequest);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        agreementService.deleteAgreement(agreementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{agreementId}/history")
    public ResponseEntity<Void> deleteAgreementForHistory(HttpServletRequest httpServletRequest, @PathVariable long agreementId) {
        boolean isAdmin = authProvider.isAdmin(httpServletRequest);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        agreementService.deleteAgreementForHistory(agreementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
