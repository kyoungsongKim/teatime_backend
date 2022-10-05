package castis.domain.model;

import java.util.Comparator;
import java.util.Date;

public class VacationHistory implements Comparator<Date> {
	public static final String STATUS_READY = "READY";
	public static final String STATUS_DONE = "DONE";
	
	private Number id;
	private Number ticketNo;
	private String userId;
	private String memo;
	private Date createdate;
	private Date senddate;
	private String status;
	
	public VacationHistory() {
		
	}

	public VacationHistory(Number id, String userId, Number ticketNo, String memo, Date createdate, Date senddate, String status) {
		super();
		this.id = id;
		this.userId = userId;
		this.ticketNo = ticketNo;
		this.memo = memo;
		this.createdate = createdate;
		this.senddate = senddate;
		this.status = status;
	}

	public Number getId() {
		return id;
	}

	public void setId(Number id) {
		this.id = id;
	}

	public Number getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(Number ticketNo) {
		this.ticketNo = ticketNo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Date getSenddate() {
		return senddate;
	}

	public void setSenddate(Date senddate) {
		this.senddate = senddate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int compare(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return date1.compareTo(date2);
	}
	
}
