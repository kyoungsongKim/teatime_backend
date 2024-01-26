package castis.domain.board.entity;

import castis.domain.board.dto.WriteDataDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;

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

    @Column(name = "brdmemo", length = 16383)
    private String brdmemo;

    @Column(name = "BRDDATE")
    private String brddate;

    @Column(name = "BRDHIT")
    private Integer brdhit;

    @Column(name = "BRDDELETEFLAG")
    private Character brddeleteflag;

    @OneToMany(mappedBy = "brdno")
    private Set<Boardreply> boardreplies = new LinkedHashSet<>();

    @Column(name = "AGREEMENTUSERID")
    private String agreementUserId;

    public void setBoardreplies(Set<Boardreply> boardreplies) {
        this.boardreplies = boardreplies;
    }

    public Board(WriteDataDto data){
        this.brdtitle = data.getTitle();
        this.brdmemo = data.getSummary();
        this.brdwriter = data.getWriter();
        this.brddate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.boardGroup = data.getBoardGroup();
        this.brdhit = 0;
        this.brddeleteflag = 'N';
        this.agreementUserId = data.getAgreementUserId();
    }

    public Board updateData(WriteDataDto data){
        this.brdtitle = data.getTitle();
        this.brdmemo = data.getSummary();
        this.brdwriter = data.getWriter();
        this.agreementUserId = data.getAgreementUserId();
        return this;
    }
    public Board deleteData(){
            this.brddeleteflag = 'Y';
            return this;
    }

    public void hitUp(){
        this.brdhit++;
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
