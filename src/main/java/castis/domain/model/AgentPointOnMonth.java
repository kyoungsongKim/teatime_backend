package castis.domain.model;

public class AgentPointOnMonth {
	private String sender;
	private String recver;
	private String memo;
	private int point;
	private String recvDate;

	public AgentPointOnMonth() {
	}

	public AgentPointOnMonth(String sender, String recver, String memo, int point, String recvDate) {
		this.sender = sender;
		this.recver = recver;
		this.memo = memo;
		this.point = point;
		this.recvDate = recvDate;
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public String getRecvDate() {
		return recvDate;
	}

	public void setRecvDate(String recvDate) {
		this.recvDate = recvDate;
	}
}