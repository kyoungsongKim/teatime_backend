package castis.domain.user;

import castis.domain.model.Users;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao {
	private JdbcTemplate jdbcTemplate;

	public UserDaoJdbc() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void add(Users user) {
		if (user != null) {
			this.jdbcTemplate.update(
					"insert into users(userid, username, realname, password,teamname,position,cellphone,email,enabled,dailyreportlist) values(?,?,?,?,?,?,?,?,?,?)",
					user.getUserid(), user.getUsername(), user.getRealname(), user.getPassword(), user.getTeamname(), user.getPosition(),
					user.getCellphone(), user.getEmail(), user.isEnabled(), user.getDailyreportlist());
		}
	}
	
	public Number update(Users user) {
		final String userUpdateSQL = "update users set password=?,email=?,dailyreportlist=?,vacationreportlist=?,realname=? WHERE userid=?";
		final String userPasssword = user.getPassword();
		final String userEmain = user.getEmail();
		final String dayReportList = user.getDailyreportlist();
		final String vacationReportList = user.getVacationReportList();
		final String realName = user.getRealname();
		final String userId = user.getUserid();
		int isSuccess = 0;
		if (user != null) {
			isSuccess = this.jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(userUpdateSQL, new String[] { "userid" });
					ps.setString(1, userPasssword);
					ps.setString(2, userEmain);
					ps.setString(3, dayReportList);
					ps.setString(4, vacationReportList);
					ps.setString(5, realName);
					ps.setString(6, userId);
					return ps;
				}
			});
		}
		return isSuccess;
	}

	public void deleteAll() {
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("delete from users");
			}
		});
	}

	private RowMapper<Users> userMapper = new RowMapper<Users>() {
		@Override
		public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
			Users user = new Users();
			user.setUserid(rs.getString("userid"));
			user.setUsername(rs.getString("username"));
			user.setRealname(rs.getString("realname"));
			user.setPassword(rs.getString("password"));
			user.setTeamname(rs.getString("teamname"));
			user.setPosition(rs.getString("position"));
			user.setCellphone(rs.getString("cellphone"));
			user.setEmail(rs.getString("email"));
			user.setEnabled(rs.getBoolean("enabled"));
			user.setDailyreportlist(rs.getString("dailyreportList"));
			user.setVacationReportList(rs.getString("vacationreportList"));
			return user;
		}
	};

	public Users get(String username) {
		return jdbcTemplate.queryForObject("select * from users where username = ?", new Object[] { username },
				this.userMapper);
	}

	public List<Users> getAll() {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("select * from users order by username");
			}
		}, this.userMapper);
	}

	public int getCount() {
		return jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
	}
}
