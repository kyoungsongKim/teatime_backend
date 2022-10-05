package castis.domain.model;

public class Team {
	private String teamname;
	private String teamdescription;

	public Team() {
	}

	public Team(String teamname, String teamdescription) {
		this.teamname = teamname;
		this.teamdescription = teamdescription;
	}

	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}

	public String getTeamdescription() {
		return teamdescription;
	}

	public void setTeamdescription(String teamdescription) {
		this.teamdescription = teamdescription;
	}
}
