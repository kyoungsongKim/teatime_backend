package castis.domain.filesystem.dto;

import castis.domain.filesystem.entity.FileMeta;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileMetaDto {

    private Long id;

    private String name;

    private String originalName;

    private Long size;

    public FileMetaDto(FileMeta fileMeta) {
        this.id = fileMeta.getId();
        this.name = fileMeta.getName();
        this.originalName = fileMeta.getOriginalName();
        this.size = fileMeta.getSize();
    }

}
