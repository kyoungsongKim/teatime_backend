package castis.domain.board.dto;

import castis.domain.board.entity.Board;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A DTO for the {@link Board} entity
 */
@Data
@Getter
@Setter
public class BoardListDto implements Serializable {
    private long size;
    private List<BoardDataDto> boardDataList;

    @Builder
    public BoardListDto(long count, int size){
        this.size = count / size;
        if(count % size != 0) {
            this.size++;
        }
    }

    public void setBoardDataList(List<Board> boards){
        boardDataList = new ArrayList();
        for(Board obj : boards){
            BoardDataDto data = new BoardDataDto(obj);
            this.boardDataList.add(data);
        }
    }
}


/*
public class Icon{
    public String path;
    @JsonProperty("class")
    public String myclass;
    public String tooltip;
}

public class Root{
    public String title;
    public String summary;
    public String author;
    public String date;
    public Object avatar;
    public String answers;
    public String upvotes;
    public ArrayList<Icon> icons;
    public ArrayList<String> tags;
}

*/