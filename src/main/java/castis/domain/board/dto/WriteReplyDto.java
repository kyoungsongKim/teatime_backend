package castis.domain.board.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class WriteReplyDto {
    private int boardNum;
    private int parentId;
    private String memo;
    private String writer;
}
