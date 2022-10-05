package castis.domain.team;

import castis.domain.model.Team;

import java.util.List;

public interface TeamDao {
	public void add(Team team);

	public void deleteAll();

	public Team get(String teamname);

	public List<Team> getAll();

	public int getCount();
}
