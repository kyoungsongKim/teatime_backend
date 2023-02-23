package castis.domain.board.dto;

import castis.domain.board.entity.Boardfile;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
public class BoardfileDto {
    public String filename;
    public String realname;
    public int filesize;

    public BoardfileDto(Boardfile file) {
        this.filename = file.getFilename();
        this.filesize = file.getFilesize();
        this.realname = file.getRealname();
    }
}
