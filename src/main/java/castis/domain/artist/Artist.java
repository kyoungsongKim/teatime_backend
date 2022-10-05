package castis.domain.artist;

public class Artist {
	private Long id; 
	private String userid; 
	private String username; 
	private String realname;
	private String teamname;
	private String onelinetitle;
	private String skills;
	private String projectlist;
	private String thumbnail;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getTeamname() {
		return teamname;
	}
	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}
	public String getOnelinetitle() {
		return onelinetitle;
	}
	public void setOnelinetitle(String onelinetitle) {
		this.onelinetitle = onelinetitle;
	}
	public String getSkills() {
		return skills;
	}
	public void setSkills(String skills) {
		this.skills = skills;
	}
	public String getProjectlist() {
		return projectlist;
	}
	public void setProjectlist(String projectlist) {
		this.projectlist = projectlist;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
}
