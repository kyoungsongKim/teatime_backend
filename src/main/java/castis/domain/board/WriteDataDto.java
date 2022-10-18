package castis.domain.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class WriteDataDto {
    private String title;
    private String summary;
    private String writer;
    private long boardGroup;
}
