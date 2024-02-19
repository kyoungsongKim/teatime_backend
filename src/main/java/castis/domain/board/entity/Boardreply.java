package castis.domain.board.entity;

import castis.domain.board.dto.WriteReplyDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "boardreply")
@Getter
@Setter
@RequiredArgsConstructor
public class Boardreply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RENO", nullable = false)
    private Integer id;

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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BRDNO", nullable = false)
    private Board brdno;

    public Boardreply(WriteReplyDto writeReplyDto, Board board){
        this.brdno = board;
        this.rewriter = writeReplyDto.getWriter();
        this.rememo = writeReplyDto.getMemo();
        this.redate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.redepth = 0;
        this.reparent = -1;
        this.reorder = 0;
        this.redeleteflag = 'N';
    }

    public Boardreply deleteData(){
        this.redeleteflag = 'Y';
        return this;
    }
}
