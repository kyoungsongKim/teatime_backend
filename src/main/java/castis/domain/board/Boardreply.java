package castis.domain.board;

import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Table(name = "boardreply")
@RequiredArgsConstructor
public class Boardreply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RENO", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "BRDNO", nullable = false)
    private Integer brdno;

    @Size(max = 10)
    @NotNull
    @Column(name = "REWRITER", nullable = false, length = 10)
    private String rewriter;

    @Size(max = 500)
    @Column(name = "REMEMO", length = 500)
    private String rememo;

    @Column(name = "REDATE")
    private String redate;

    @Column(name = "REPARENT")
    private Integer reparent;

    @Column(name = "REDEPTH")
    private Integer redepth;

    @Column(name = "REORDER")
    private Integer reorder;

    @Column(name = "REDELETEFLAG")
    private Character redeleteflag;


    public Boardreply(WriteReplyDto writeReplyDto){
        this.brdno = writeReplyDto.getBoardNum();
        this.rewriter = writeReplyDto.getWriter();
        this.rememo = writeReplyDto.getMemo();
        this.redate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        this.redepth = 0;
        this.reparent = -1;
        this.reorder = 0;
        this.redeleteflag = 'N';
    }

    public Boardreply deleteData(){
        this.redeleteflag = 'Y';
        return this;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBrdno() {
        return brdno;
    }

    public void setBrdno(Integer brdno) {
        this.brdno = brdno;
    }

    public String getRewriter() {
        return rewriter;
    }

    public void setRewriter(String rewriter) {
        this.rewriter = rewriter;
    }

    public String getRememo() {
        return rememo;
    }

    public void setRememo(String rememo) {
        this.rememo = rememo;
    }

    public String getRedate() {
        return redate;
    }

    public void setRedate(String redate) {
        this.redate = redate;
    }

    public Integer getReparent() {
        return reparent;
    }

    public void setReparent(Integer reparent) {
        this.reparent = reparent;
    }

    public Integer getRedepth() {
        return redepth;
    }

    public void setRedepth(Integer redepth) {
        this.redepth = redepth;
    }

    public Integer getReorder() {
        return reorder;
    }

    public void setReorder(Integer reorder) {
        this.reorder = reorder;
    }

}