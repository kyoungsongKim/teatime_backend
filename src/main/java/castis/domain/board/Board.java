package castis.domain.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@Table(name = "board")
@Entity
@RequiredArgsConstructor
public class Board {
    @Id
    @Column(name="BRDNO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardNum;

    @Column(name="BGNO")
    private Long boardGroup;

    @Column(name = "BRDTITLE")
    private String brdtitle;

    @Column(name = "BRDWRITER", length = 20)
    private String brdwriter;

    @Column(name = "brdmemo", length = 20000)
    private String brdmemo;

    @Column(name = "BRDDATE")
    private String brddate;

    @Column(name = "BRDHIT")
    private Integer brdhit;

    @Column(name = "BRDDELETEFLAG")
    private Character brddeleteflag;

    public Board(WriteDataDto data){
        this.brdtitle = data.getTitle();
        this.brdmemo = data.getSummary();
        this.brdwriter = data.getWriter();
        this.brddate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        this.boardGroup = data.getBoardGroup();
        this.brdhit = 0;
        this.brddeleteflag = 'N';
    }

    public void setBrddeleteflag(Character brddeleteflag) {
        this.brddeleteflag = brddeleteflag;
    }

    public void setBrdhit(Integer brdhit) {
        this.brdhit = brdhit;
    }

    public void setBrddate(String brddate) {
        this.brddate = brddate;
    }

    public void setBrdmemo(String brdmemo) {
        this.brdmemo = brdmemo;
    }

    public void setBrdwriter(String brdwriter) {
        this.brdwriter = brdwriter;
    }

    public void setBrdtitle(String brdtitle) {
        this.brdtitle = brdtitle;
    }
}
