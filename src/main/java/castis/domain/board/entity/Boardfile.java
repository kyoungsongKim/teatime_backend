package castis.domain.board.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@RequiredArgsConstructor
@Table(name = "boardfile")
public class Boardfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILENO", nullable = false)
    private Integer id;

    @Column(name = "BRDNO")
    private Integer brdno;

    @Size(max = 100)
    @Column(name = "FILENAME", length = 100)
    private String filename;

    @Size(max = 30)
    @Column(name = "REALNAME", length = 30)
    private String realname;

    @Column(name = "FILESIZE")
    private Integer filesize;

    public Boardfile(int brdno, MultipartFile file, String prefix){
        this.brdno = brdno;
        this.filename = prefix.isEmpty() ? file.getOriginalFilename() : prefix + file.getOriginalFilename();
        this.filesize = Long.valueOf(file.getSize()).intValue();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        this.realname =  ft.format(new Date()) + (int) (Math.random() * 10);
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public Integer getFilesize() {
        return filesize;
    }

    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
    }

}