package castis.domain.monthlysales.dto;

import lombok.Data;

@Data
public class CBankHistoryDto {

    private int id;
    private String type;
    private String sendName;
    private String sendAccount;
    private String recvName;
    private String recvAccount;
    private String history;
    private String amount;
    private String balance;
    private String date;
    private String status;
    private String memo;
    private boolean income;
}
