package castis.domain.assistance.dto;

import castis.domain.assistance.constant.AppliedAssistanceStatus;
import lombok.Getter;

@Getter
public class UpdateAssistanceApplyStatusBody {

    private AppliedAssistanceStatus status;
}
