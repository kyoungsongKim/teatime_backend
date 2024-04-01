package castis.domain.vacation.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class UpdateVacationBody {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventEndDate;

    private Float amount;

    private String reason;

    private String adminMemo;

    private String type;

    private String userId;
}
