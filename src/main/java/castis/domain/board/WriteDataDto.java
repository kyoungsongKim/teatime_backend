package castis.domain.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@RequiredArgsConstructor
public class WriteDataDto {
    private String title;
    private String summary;
    private String writer;
    private long boardGroup;
    private long boardNum;
}
