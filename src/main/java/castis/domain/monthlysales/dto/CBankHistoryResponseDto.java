package castis.domain.monthlysales.dto;

import lombok.Data;

import java.util.List;

@Data
public class CBankHistoryResponseDto {
    private int resultCode;
    private String resultMsg;
    private String now;
    private int count;
    private List<CBankHistoryDto> history;
}
