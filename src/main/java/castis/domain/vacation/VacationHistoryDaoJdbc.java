package castis.domain.vacation;

import castis.domain.model.VacationHistory;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VacationHistoryDaoJdbc implements VacationHistoryDao {
	private JdbcTemplate jdbcTemplate;
	
	public VacationHistoryDaoJdbc() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Number addVacationHistory(VacationHistory vacationHistory) {
		final String vacationHistoryInsertSQL = "insert into vacation_history(ticketNo,userId,memo,createdate,senddate,status) values(?,?,?,?,?,?)";
		final Number ticketNo = vacationHistory.getTicketNo();
		final String userId = vacationHistory.getUserId();
		final String memo = vacationHistory.getMemo();
		final Date createdate = vacationHistory.getCreatedate();
		final Date senddate = vacationHistory.getSenddate();
		final String status = vacationHistory.getStatus();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (vacationHistory != null) {
			this.jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(vacationHistoryInsertSQL, new String[] { "id" });
					ps.setLong(1, ticketNo.longValue());
					ps.setString(2, userId);
					ps.setString(3, memo);
					ps.setObject(4, new java.sql.Timestamp(createdate.getTime()));
					if ( senddate == null ) {
						ps.setObject(5, null);
					} else {
						ps.setObject(5, new java.sql.Timestamp(senddate.getTime()));
					}
					ps.setString(6, status);
					return ps;
				}
			}, keyHolder);
		}
		return keyHolder.getKey();
	}

	@Override
	public Number update(VacationHistory vacationHistory) {
		final String vacationHistoryInsertSQL = "update vacation_history set memo=?,senddate=?,status=? WHERE id=?";
		final String memo = vacationHistory.getMemo();
		final Date senddate = vacationHistory.getSenddate();
		final String status = vacationHistory.getStatus();
		final Long no = vacationHistory.getId().longValue();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		int isSuccess= 0;
		if (vacationHistory != null) {
			isSuccess = this.jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(vacationHistoryInsertSQL, new String[] { "id" });
					ps.setString(1, memo);
					if ( senddate == null ) {
						ps.setObject(2, null);
					} else {
						ps.setObject(2, new java.sql.Timestamp(senddate.getTime()));
					}
					ps.setString(3, status);
					ps.setLong(4, no);
					return ps;
				}
			}, keyHolder);
		}
		return isSuccess;
	}

	private RowMapper<VacationHistory> userMapper = new RowMapper<VacationHistory>() {
		@Override
		public VacationHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
			VacationHistory vacationHistory = new VacationHistory();
			vacationHistory.setId(rs.getLong("id"));
			vacationHistory.setTicketNo(rs.getLong("ticketNo"));
			vacationHistory.setUserId(rs.getString("userId"));
			vacationHistory.setMemo(rs.getString("memo"));
			vacationHistory.setCreatedate(rs.getTimestamp("createdate"));
			vacationHistory.setSenddate(rs.getTimestamp("senddate"));
			vacationHistory.setStatus(rs.getString("status"));
			return vacationHistory;
		}
	};
	
	@Override
	public VacationHistory get(Number vacationHistoryNumber) {
		return jdbcTemplate.queryForObject("select * from vacation_history where id = ?", new Object[] { vacationHistoryNumber }, this.userMapper);
	}

	@Override
	public List<VacationHistory> getAllVacationHistory() {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("select * from vacation_history order by id");
			}
		}, this.userMapper);
	}
	@Override
	public List<VacationHistory> getVacationHistoryBySendDate(Date sendDate) {
		final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String qeuryStr = "select * from vacation_history where senddate between '" + format.format(sendDate) + "' and '" + format.format(sendDate) + "'";
				return con.prepareStatement(qeuryStr);
			}
		}, this.userMapper);
	}
	
	@Override
	public List<VacationHistory> getVacationHistoryByYear(String userId, int year) {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {  
				String queryStr = "select * from vacation_history where YEAR(senddate) = " + year + " AND userId = '" + userId +"'";
				return con.prepareStatement(queryStr);
			}
		}, this.userMapper);
	}

	@Override
	public List<VacationHistory> getVacationHistoryByUser(String userId) {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {  
				String queryStr = "select * from vacation_history where userId = '" + userId +"'";
				return con.prepareStatement(queryStr);
			}
		}, this.userMapper);
	}

	@Override
	public  List<VacationHistory> getVacationHistoryTicketNo(Long ticketNo) {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {  
				String queryStr = "select * from vacation_history where ticketNo = '" + ticketNo +"'";
				return con.prepareStatement(queryStr);
			}
		}, this.userMapper);
	}
	
	public int delete(Number no) {
		return jdbcTemplate.update("delete from vacation_history where id = ?", no.longValue());
	}
}
