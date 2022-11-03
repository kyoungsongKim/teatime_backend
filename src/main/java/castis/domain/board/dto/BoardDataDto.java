package castis.domain.board.dto;

import castis.domain.board.entity.Board;
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

    public String fileName;
    public String saveName;
    public int fileSize;

    public int repliesCount;

    public BoardDataDto(Board board){
        this.bdNum = board.getBoardNum();
        this.title = board.getBrdtitle();
        this.author = board.getBrdwriter();
        this.date = board.getBrddate();
        this.avatar = false;
        this.upvotes = board.getBrdhit().toString();
        this.summary = board.getBrdmemo();
        this.repliesCount = board.getBoardreplies().size();
    }
}