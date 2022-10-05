package castis.domain.model;

public class UserStatOnMonth {
	private String username;
	private String site;
	private String project;
	private Float nmd;
	private Float emd;

	public UserStatOnMonth() {
	}

	public UserStatOnMonth(String username, String site, String project, Float nmd, Float emd) {
		this.username = username;
		this.site = site;
		this.project = project;
		this.nmd = nmd;
		this.emd = emd;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public Float getNmd() {
		return nmd;
	}

	public void setNmd(float nmd) {
		this.nmd = nmd;
	}

	public Float getEmd() {
		return emd;
	}

	public void setEmd(float emd) {
		this.emd = emd;
	}
}