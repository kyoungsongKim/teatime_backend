package castis.domain.authorities;

import castis.domain.model.Authorities;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AuthoritiesDaoJdbc implements AuthoritiesDao {
	private JdbcTemplate jdbcTemplate;

	public AuthoritiesDaoJdbc() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void add(Authorities authorities) {
		if (authorities != null) {
			this.jdbcTemplate.update("insert into authorities(username,authority) values(?,?)", authorities.getUsername(), authorities.getAuthority());
		}
	}

	public void deleteAll() {
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("delete from authorities");
			}
		});
	}

	private RowMapper<Authorities> userMapper = new RowMapper<Authorities>() {
		@Override
		public Authorities mapRow(ResultSet rs, int rowNum) throws SQLException {
			Authorities authorities = new Authorities();
			authorities.setUsername(rs.getString("username"));
			authorities.setAuthority(rs.getString("authority"));
			return authorities;
		}
	};

	public Authorities get(String username) {
		return jdbcTemplate.queryForObject("select * from authorities where username = ?", new Object[] { username }, this.userMapper);
	}

	public List<Authorities> getAll() {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("select * from authorities order by username");
			}
		}, this.userMapper);
	}

	public int getCount() {
		return jdbcTemplate.queryForObject("select count(*) from authorities", Integer.class);
	}
}
