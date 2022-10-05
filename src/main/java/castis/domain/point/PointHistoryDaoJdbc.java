package castis.domain.point;

import castis.domain.model.PointHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class PointHistoryDaoJdbc implements PointHistoryDao {
	private JdbcTemplate jdbcTemplate;
	
	public PointHistoryDaoJdbc() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Number addPointHistory(PointHistory pointHistory) {
		final String pointHistoryInsertSQL = "insert into point_history(point,code,memo,sender,recver,createdate,usedate) values(?,?,?,?,?,?,?)";
		final int pointHistoryPoint = pointHistory.getPoint();
		final String pointHistoryCode = pointHistory.getCode();
		final String pointHistoryMemo = pointHistory.getMemo();
		final String pointHistorySender = pointHistory.getSender();
		final String pointHistoryRecver = pointHistory.getRecver();
		final Date pointHistoryCreateDate = pointHistory.getCreatedate();
		final Date pointHistoryUseDate = pointHistory.getUsedate();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (pointHistory != null) {
			this.jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(pointHistoryInsertSQL, new String[] { "id" });
					ps.setInt(1, pointHistoryPoint);
					ps.setString(2, pointHistoryCode);
					ps.setString(3, pointHistoryMemo);
					ps.setString(4, pointHistorySender);
					ps.setString(5, pointHistoryRecver);
					ps.setObject(6, new java.sql.Timestamp(pointHistoryCreateDate.getTime()));
					if ( pointHistoryUseDate == null ) {
						ps.setObject(7, null);
					} else {
						ps.setObject(7, new java.sql.Timestamp(pointHistoryUseDate.getTime()));
					}
					return ps;
				}
			}, keyHolder);
		}
		return keyHolder.getKey();
	}

	@Override
	public Number update(PointHistory pointHistory) {
		final String pointHistoryInsertSQL = "update point_history set point=?,code=?,memo=?,sender=?,recver=?,createdate=?,usedate=? WHERE id=?";
		final int pointHistoryPoint = pointHistory.getPoint();
		final String pointHistoryCode = pointHistory.getCode();
		final String pointHistoryMemo = pointHistory.getMemo();
		final String pointHistorySender = pointHistory.getSender();
		final String pointHistoryRecver = pointHistory.getRecver();
		final Date pointHistoryCreateDate = pointHistory.getCreatedate();
		final Date pointHistoryUseDate = pointHistory.getUsedate();
		final Long no = pointHistory.getId().longValue();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		int isSuccess= 0;
		if (pointHistory != null) {
			isSuccess = this.jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(pointHistoryInsertSQL, new String[] { "id" });
					ps.setInt(1, pointHistoryPoint);
					ps.setString(2, pointHistoryCode);
					ps.setString(3, pointHistoryMemo);
					ps.setString(4, pointHistorySender);
					ps.setString(5, pointHistoryRecver);
					ps.setObject(6, new java.sql.Timestamp(pointHistoryCreateDate.getTime()));
					if ( pointHistoryUseDate == null ) {
						ps.setObject(7, null);
					} else {
						ps.setObject(7, new java.sql.Timestamp(pointHistoryUseDate.getTime()));
					}
					ps.setLong(8, no);
					return ps;
				}
			}, keyHolder);
		}
		return isSuccess;
	}

	private RowMapper<PointHistory> userMapper = new RowMapper<PointHistory>() {
		@Override
		public PointHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
			PointHistory pointHistory = new PointHistory();
			pointHistory.setId(rs.getLong("id"));
			pointHistory.setPoint(rs.getInt("point"));
			pointHistory.setCode(rs.getString("code"));
			pointHistory.setMemo(rs.getString("memo"));
			pointHistory.setSender(rs.getString("sender"));
			pointHistory.setRecver(rs.getString("recver"));
			pointHistory.setCreatedate(rs.getTimestamp("createdate"));
			pointHistory.setUsedate(rs.getTimestamp("usedate"));
			return pointHistory;
		}
	};
	
	@Override
	public PointHistory get(Number pointHistoryNumber) {
		return jdbcTemplate.queryForObject("select * from point_history where id = ?", new Object[] { pointHistoryNumber }, this.userMapper);
	}

	@Override
	public List<PointHistory> getAllPointHistory() {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("select * from point_history order by id");
			}
		}, this.userMapper);
	}

	@Override
	public List<PointHistory> getPointHistoryByYear(String recver, int year) {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {  
				String queryStr = "select * from point_history where YEAR(usedate) = " + year + " AND recver = '" + recver +"'";
				return con.prepareStatement(queryStr);
			}
		}, this.userMapper);
	}

	public List<PointHistory> getPointHistoryByYearAndMonth(final int year, final int month) {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {  
				String queryStr = "select * from point_history where YEAR(usedate) = " + year + " AND  MONTH(usedate) = " + month;
				return con.prepareStatement(queryStr);
			}
		}, this.userMapper);
	}
	
	@Override
	public PointHistory getByCode(String code) {
		return jdbcTemplate.queryForObject("select * from point_history where code = ?", new Object[] { code }, this.userMapper);
	}

	@Override
	public List<PointHistory> getPointHistoryByUser(String recver) {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {  
				String queryStr = "select * from point_history where recver = '" + recver +"'";
				return con.prepareStatement(queryStr);
			}
		}, this.userMapper);
	}
}
