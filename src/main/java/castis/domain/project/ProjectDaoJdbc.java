package castis.domain.project;

import castis.domain.model.Project;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ProjectDaoJdbc implements ProjectDao {
	private JdbcTemplate jdbcTemplate;

	public ProjectDaoJdbc() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void add(Project project) {
		if (project != null) {
			this.jdbcTemplate.update("insert into project(projectname,site,description,startdate,enddate,bgcolor) values(?,?,?,?,?,?)", project.getProjectname(),
					project.getSite(), project.getDescription(), project.getStartdate(), project.getEnddate(), project.getBgColor());
		}
	}
	
	public Number updateEndDate(final Date endDate, final String site, final String projectname) {
		final String projectUpdateSQL = "update project set enddate=? WHERE site=? and projectname=?";
		int isSuccess = 0;
		isSuccess = this.jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(projectUpdateSQL);
				ps.setObject(1, endDate);
				ps.setString(2, site);
				ps.setString(3, projectname);
				return ps;
			}
		});
		return isSuccess;
	}
	
	public int deleteSiteAndProjectName(String site, String projectName) {
		return jdbcTemplate.update("delete from project where site = ? and projectname = ?", site, projectName);
	}

	public void deleteAll() {
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("delete from project");
			}
		});
	}

	private RowMapper<Project> userMapper = new RowMapper<Project>() {
		@Override
		public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
			Project project = new Project();
			project.setProjectname(rs.getString("projectname"));
			project.setSite(rs.getString("site"));
			project.setDescription(rs.getString("description"));
			project.setStartdate(rs.getDate("startdate"));
			project.setEnddate(rs.getDate("enddate"));
			project.setBgColor(rs.getString("bgcolor"));
			return project;
		}
	};

	public Project get(String projectname) {
		return jdbcTemplate.queryForObject("select * from project where projectname = ?", new Object[] { projectname }, this.userMapper);
	}
	
	public Project getBySite(String site) {
		return jdbcTemplate.queryForObject("select * from project where site = ? limit 1", new Object[] { site }, this.userMapper);
	}

	public List<Project> getAll() {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("select * from project order by projectname");
			}
		}, this.userMapper);
	}

	public int getCount() {
		return jdbcTemplate.queryForObject("select count(*) from project", Integer.class);
	}
}
