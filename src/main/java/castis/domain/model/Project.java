package castis.domain.model;

import java.util.Date;

public class Project implements Comparable<Project>{
	private String projectname;
	private String site;
	private String description;
	private Date startdate;
	private Date enddate;
	private String bgColor;

	public Project() {
	}

	public Project(String projectname, String site, String description, Date startdate, Date enddate, String bgColor) {
		this.projectname = projectname;
		this.site = site;
		this.description = description;
		this.startdate = startdate;
		this.enddate = enddate;
		this.bgColor = bgColor;
	}

	public String getProjectname() {
		return projectname;
	}

	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	@Override
	public int compareTo(Project o) {
		return site.compareTo(o.getSite());
	}
}
