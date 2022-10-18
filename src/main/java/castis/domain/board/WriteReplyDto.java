package castis.domain.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class WriteReplyDto {
    private String memo;
    private String writer;
}
