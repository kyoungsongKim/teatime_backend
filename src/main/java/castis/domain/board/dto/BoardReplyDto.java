package castis.domain.board.dto;

import castis.domain.board.entity.Boardreply;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class BoardReplyDto {
    List<BoardReplyData> replies;

    public BoardReplyDto(List<Boardreply> list){
        replies = new ArrayList<>();
        for (Boardreply v: list) {
            BoardReplyData data = new BoardReplyData(v);
            replies.add(data);
        }
    }
}

@Data
@Getter
@Setter
class BoardReplyData {
    public String message;
    public String author;
    public String date;
    public Object avatar;
    public int depth;
    public int id;

    public BoardReplyData(Boardreply reply){
        this.id = reply.getId();
        this.message = reply.getRememo();
        this.author = reply.getRewriter();
        this.date = String.valueOf(reply.getRedate());
        this.avatar = false;
        this.depth = reply.getRedepth();
    }

}