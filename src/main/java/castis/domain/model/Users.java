package castis.domain.model;

public class Users {
	private String userid;
	private String password;
	private String username;
	private String realname;
	private String teamname;
	private String position;
	private String cellphone;
	private String email;
	private boolean enabled;
	private String dailyreportlist;
	private String vacationReportList;

	public Users() {
	}
	
	public Users(String userid, String username, String realname, String password, String teamname, String position, String cellphone, String email, boolean enabled, String dailyreportlist, String vacationReportList) {
		this.userid = userid;
		this.username = username;
		this.password = password;
		this.realname = realname;
		this.teamname = teamname;
		this.position = position;
		this.cellphone = cellphone;
		this.email = email;
		this.enabled = enabled;
		this.dailyreportlist = dailyreportlist;
		this.vacationReportList = vacationReportList;
	}
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	
	public String getDailyreportlist() {
		return dailyreportlist;
	}

	public void setDailyreportlist(String dailyreportlist) {
		this.dailyreportlist = dailyreportlist;
	}

	public String getVacationReportList() {
		return vacationReportList;
	}

	public void setVacationReportList(String vacationReportList) {
		this.vacationReportList = vacationReportList;
	}

	@Override
	public String toString() {
		return "Users [userid=" + userid + ", password=" + password + ", username=" + username + ", teamname=" + teamname + ", position=" + position
				+ ", cellphone=" + cellphone + ", email=" + email + ", enabled=" + enabled + ", dailyreportlist=" + dailyreportlist + ", vacationReportList="+ vacationReportList +"]";
	}
	
	
}
