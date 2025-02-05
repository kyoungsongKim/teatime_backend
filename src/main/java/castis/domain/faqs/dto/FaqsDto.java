package castis.domain.faqs.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class FaqsDto implements Serializable {
    public Integer id;
    public String name;
    public String description;
}
