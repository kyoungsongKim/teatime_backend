package castis.domain.vacation;

import castis.domain.model.Vacation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class VacationDaoJdbc implements VacationDao {
	private JdbcTemplate jdbcTemplate;

	public VacationDaoJdbc() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void add(Vacation vacation) {
		if (vacation != null) {
			this.jdbcTemplate.update("insert into vacation(username,vacationyear,vacationreward,vacationspecial,resetdate) values(?,?,?,?,?)",
					vacation.getUsername(), vacation.getVacationyear(), vacation.getVacationreward(), vacation.getVacationspeacial(), vacation.getResetdate());
		}
	}

	public void deleteAll() {
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("delete from vacation");
			}
		});
	}

	private RowMapper<Vacation> userMapper = new RowMapper<Vacation>() {
		@Override
		public Vacation mapRow(ResultSet rs, int rowNum) throws SQLException {
			Vacation vacation = new Vacation();
			vacation.setUsername(rs.getString("username"));
			vacation.setVacationyear(rs.getInt("vacationyear"));
			vacation.setVacationreward(rs.getInt("vacationreward"));
			vacation.setVacationspeacial(rs.getInt("vacationspecial"));
			vacation.setResetdate(rs.getDate("resetdate"));
			return vacation;
		}
	};

	public Vacation get(String username) {
		return jdbcTemplate.queryForObject("select * from vacation where username = ?", new Object[] { username }, this.userMapper);
	}

	public List<Vacation> getAll() {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("select * from vacation order by username");
			}
		}, this.userMapper);
	}

	public int getCount() {
		return jdbcTemplate.queryForObject("select count(*) from vacation", Integer.class);
	}
}
