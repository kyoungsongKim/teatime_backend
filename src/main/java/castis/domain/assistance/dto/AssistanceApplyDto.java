package castis.domain.assistance.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import castis.domain.assistance.constant.AppliedAssistanceStatus;
import castis.domain.assistance.entity.AssistanceApply;
import castis.domain.filesystem.dto.FileMetaDto;
import castis.domain.filesystem.entity.FileMeta;
import castis.domain.user.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AssistanceApplyDto {
    private Integer id;

    private String content;

    private List<FileMetaDto> files;

    private AppliedAssistanceStatus status;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private UserDto applier;

    private UserDto receiver;

    private AssistanceDto assistance;

    private AssistanceReviewDto review;

    public AssistanceApplyDto(AssistanceApply assistanceApply) {
        this.id = assistanceApply.getId();
        this.content = assistanceApply.getContent();
        List<FileMeta> files = assistanceApply.getFiles();
        if (files != null) {
            this.files = assistanceApply.getFiles().stream().map(FileMetaDto::new)
                    .collect(Collectors.toList());
        }
        this.status = assistanceApply.getStatus();
        this.createdDate = assistanceApply.getCreatedDate();
        this.updatedDate = assistanceApply.getUpdatedDate();
        if (assistanceApply.getApplier() != null) {
            this.applier = new UserDto(assistanceApply.getApplier(), false);
        }
        if (assistanceApply.getReceiver() != null) {
            this.receiver = new UserDto(assistanceApply.getReceiver(), false);
        }
        if (assistanceApply.getAssistance() != null) {
            this.assistance = new AssistanceDto(assistanceApply.getAssistance());
        }
        if (assistanceApply.getReview() != null) {
            this.review = new AssistanceReviewDto(assistanceApply.getReview());
        }
    }
}
