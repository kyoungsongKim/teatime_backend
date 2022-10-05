package castis.domain.team;

import castis.domain.model.Team;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class TeamDaoJdbc implements TeamDao {
	private JdbcTemplate jdbcTemplate;

	public TeamDaoJdbc() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void add(Team team) {
		if (team != null) {
			this.jdbcTemplate.update("insert into team(teamname,teamdescription) values(?,?)", team.getTeamname(), team.getTeamdescription());
		}
	}

	public void deleteAll() {
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("delete from team");
			}
		});
	}

	private RowMapper<Team> userMapper = new RowMapper<Team>() {
		@Override
		public Team mapRow(ResultSet rs, int rowNum) throws SQLException {
			Team team = new Team();
			team.setTeamname(rs.getString("teamname"));
			team.setTeamdescription(rs.getString("teamdescription"));
			return team;
		}
	};

	public Team get(String teamname) {
		return jdbcTemplate.queryForObject("select * from team where teamname = ?", new Object[] { teamname }, this.userMapper);
	}

	public List<Team> getAll() {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("select * from team order by teamname");
			}
		}, this.userMapper);
	}

	public int getCount() {
		return jdbcTemplate.queryForObject("select count(*) from team", Integer.class);
	}
}
