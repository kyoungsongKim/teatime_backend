package castis.domain.board;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Data
@Getter
@Setter
public class BoardDataDto implements Serializable {
    public long bdNum;
    public String title;
    public String summary;
    public String author;
    public String date;
    public Object avatar;
    public String answers;
    public String upvotes;

    public BoardDataDto(Board board){
        this.bdNum = board.getBoardNum();
        this.title = board.getBrdtitle();
        this.author = board.getBrdwriter();
        this.avatar = false;
        this.upvotes = board.getBrdhit().toString();
        this.summary = board.getBrdmemo();
    }
}