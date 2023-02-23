package castis.domain.board.dto;

import castis.domain.board.entity.Board;
import castis.domain.board.entity.Boardfile;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class BoardDataDto implements Serializable {
    public long bdNum;
    public String title;
    public String summary;

    public List<SummaryInfo> summaryLists;
    public String author;
    public String date;
    public Object avatar;
    public String answers;
    public String upvotes;

    public int repliesCount;

    public String agreementUserId;

    public List<BoardfileDto> boardFiles = new ArrayList<>();

    public BoardDataDto(Board board){
        this.bdNum = board.getBoardNum();
        this.title = board.getBrdtitle();
        this.author = board.getBrdwriter();
        this.date = board.getBrddate();
        this.avatar = false;
        this.upvotes = board.getBrdhit().toString();
        this.summary = board.getBrdmemo();
        this.repliesCount = board.getBoardreplies().size();
        this.summaryLists = new ArrayList<>();
        this.agreementUserId = board.getAgreementUserId();
    }

    public void setBoardfiles(List<Boardfile> files) {
        for (Boardfile file :
                files) {
            BoardfileDto boardfileDto = new BoardfileDto(file);
            boardFiles.add(boardfileDto);
        }
    }

    public void addSummary(String type, String summary) {
        SummaryInfo info = new SummaryInfo();
        info.type = type;
        info.summary = summary;
        this.summaryLists.add(info);
    }

    @NoArgsConstructor
    class SummaryInfo {
        public String type;
        public String summary;
    }

}
