package castis.domain.model;

import java.util.Comparator;
import java.util.Date;

public class PointHistory implements Comparator<Date> {
	private Number id;
	private int point;
	private String code;
	private String memo;
	private String sender;
	private String recver;
	private Date createdate;
	private Date usedate;

	
	public PointHistory(int point, String code, String memo, String sender, String recver, Date createdate,
			Date usedate) {
		super();
		this.point = point;
		this.memo = memo;
		this.code = code;
		this.sender = sender;
		this.recver = recver;
		this.createdate = createdate;
		this.usedate = usedate;
	}


	public PointHistory() {
	}


	public Number getId() {
		return id;
	}


	public void setId(Number id) {
		this.id = id;
	}


	public int getPoint() {
		return point;
	}


	public void setPoint(int point) {
		this.point = point;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getSender() {
		return sender;
	}


	public void setSender(String sender) {
		this.sender = sender;
	}


	public String getRecver() {
		return recver;
	}


	public void setRecver(String recver) {
		this.recver = recver;
	}


	public Date getCreatedate() {
		return createdate;
	}


	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}


	public Date getUsedate() {
		return usedate;
	}


	public void setUsedate(Date usedate) {
		this.usedate = usedate;
	}


	public String getMemo() {
		return memo;
	}


	public void setMemo(String memo) {
		this.memo = memo;
	}


	@Override
	public int compare(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return date1.compareTo(date2);
	}
	
}
