package castis.domain.model;

import java.util.Comparator;
import java.util.Date;

public class Ticket implements Comparator<Date> {
	private Number no;
	private String teamname;
	private String username;
	private String projectname;
	private String title;
	private String subtitle;
	private String content;
	private Float nmd;
	private Float emd;
	private String history;
	private String attachedfile;
	private Date starttime;
	private Date endtime;

	public Ticket() {
	}
	
	public Ticket(String teamname, String username, String projectname, String title, String subtitle, String content, Float nmd, Float emd,
			String history, String attachedfile, Date starttime, Date endtime) {
		super();
		this.teamname = teamname;
		this.username = username;
		this.projectname = projectname;
		this.title = title;
		this.subtitle = subtitle;
		this.content = content;
		this.nmd = nmd;
		this.emd = emd;
		this.history = history;
		this.attachedfile = attachedfile;
		this.starttime = starttime;
		this.endtime = endtime;
	}

	public Number getNo() {
		return no;
	}

	public void setNo(Number no) {
		this.no = no;
	}

	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProjectname() {
		return projectname;
	}

	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Float getNmd() {
		return nmd;
	}

	public void setNmd(Float nmd) {
		this.nmd = nmd;
	}

	public Float getEmd() {
		return emd;
	}

	public void setEmd(Float emd) {
		this.emd = emd;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public String getAttachedfile() {
		return attachedfile;
	}

	public void setAttachedfile(String attachedfile) {
		this.attachedfile = attachedfile;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getEndtime() {
		return endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	@Override
	public String toString() {
		return "Ticket [no=" + no + ", teamname=" + teamname + ", username=" + username + ", projectname=" + projectname + ", title=" + title + ", subtitle="
				+ subtitle + ", content=" + content + ", nmd=" + nmd + ", emd=" + emd + ", history=" + history + ", attachedfile=" + attachedfile
				+ ", starttime=" + starttime + ", endtime=" + endtime + "]";
	}

	@Override
	public int compare(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return date1.compareTo(date2);
	}
	
}
