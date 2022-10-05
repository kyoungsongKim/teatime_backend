package castis.domain.ticket;

import castis.domain.model.Ticket;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketDaoJdbc implements TicketDao {
	private JdbcTemplate jdbcTemplate;
	
	public TicketDaoJdbc() {
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Number add(Ticket ticket) {
		final String ticketInsertSQL = "insert into ticket(teamname,username,projectname,title,subtitle,content,nmd,emd,history,attachedfile,starttime,endtime) values(?,?,?,?,?,?,?,?,?,?,?,?)";
		final String ticketName = ticket.getTeamname();
		final String ticketUserName = ticket.getUsername();
		final String ticketProjectName = ticket.getProjectname();
		final String ticketTitle = ticket.getTitle();
		final String ticketSubTitle = ticket.getSubtitle();
		final String ticketContent = ticket.getContent();
		final Float ticketNmd = ticket.getNmd();
		final Float ticketEmd = ticket.getEmd();
		final String ticketHistory = ticket.getHistory();
		final String ticketAttachedFile = ticket.getAttachedfile();
		final Date starttime = ticket.getStarttime();
		final Date endtime = ticket.getEndtime();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (ticket != null) {
			this.jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(ticketInsertSQL, new String[] { "no" });
					ps.setString(1, ticketName);
					ps.setString(2, ticketUserName);
					ps.setString(3, ticketProjectName);
					ps.setString(4, ticketTitle);
					ps.setString(5, ticketSubTitle);
					ps.setString(6, ticketContent);
					ps.setFloat(7, ticketNmd);
					ps.setFloat(8, ticketEmd);
					ps.setString(9, ticketHistory);
					ps.setString(10, ticketAttachedFile);
					ps.setObject(11, new java.sql.Timestamp(starttime.getTime()));
					ps.setObject(12, new java.sql.Timestamp(endtime.getTime()));
					return ps;
				}
			}, keyHolder);
		}
		return keyHolder.getKey();
	}

	public Number update(Ticket ticket) {
		final String ticketInsertSQL = "update ticket set teamname=?,username=?,projectname=?,title=?,subtitle=?,content=?,nmd=?,emd=?,history=?,attachedfile=?,starttime=?,endtime=? WHERE no=?";
		final String ticketName = ticket.getTeamname();
		final String ticketUserName = ticket.getUsername();
		final String ticketProjectName = ticket.getProjectname();
		final String ticketTitle = ticket.getTitle();
		final String ticketSubTitle = ticket.getSubtitle();
		final String ticketContent = ticket.getContent();
		final Float ticketNmd = ticket.getNmd();
		final Float ticketEmd = ticket.getEmd();
		final String ticketHistory = ticket.getHistory();
		final String ticketAttachedFile = ticket.getAttachedfile();
		final Date starttime = ticket.getStarttime();
		final Date endtime = ticket.getEndtime();
		final Long no = ticket.getNo().longValue();
		int isSuccess = 0;
		if (ticket != null) {
			isSuccess = this.jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(ticketInsertSQL, new String[] { "no" });
					ps.setString(1, ticketName);
					ps.setString(2, ticketUserName);
					ps.setString(3, ticketProjectName);
					ps.setString(4, ticketTitle);
					ps.setString(5, ticketSubTitle);
					ps.setString(6, ticketContent);
					ps.setFloat(7, ticketNmd);
					ps.setFloat(8, ticketEmd);
					ps.setString(9, ticketHistory);
					ps.setString(10, ticketAttachedFile);
					ps.setObject(11, new java.sql.Timestamp(starttime.getTime()));
					ps.setObject(12, new java.sql.Timestamp(endtime.getTime()));
					ps.setLong(13, no);
					return ps;
				}
			});
		}
		return isSuccess;
	}
	
	public Number updateTime(Ticket ticket){
		final String ticketInsertSQL = "update ticket set starttime=?,endtime=? WHERE no=?";
		final Date starttime = ticket.getStarttime();
		final Date endtime = ticket.getEndtime();
		final Long no = ticket.getNo().longValue();
		int isSuccess = 0;
		if (ticket != null) {
			isSuccess = this.jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(ticketInsertSQL, new String[] { "no" });
					ps.setObject(1, new java.sql.Timestamp(starttime.getTime()));
					ps.setObject(2, new java.sql.Timestamp(endtime.getTime()));
					ps.setLong(3, no);
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
				return con.prepareStatement("delete from ticket");
			}
		});
	}

	private RowMapper<Ticket> userMapper = new RowMapper<Ticket>() {
		@Override
		public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
			Ticket ticket = new Ticket();
			ticket.setNo(rs.getLong("no"));
			ticket.setTeamname(rs.getString("teamname"));
			ticket.setUsername(rs.getString("username"));
			ticket.setProjectname(rs.getString("projectname"));
			ticket.setTitle(rs.getString("title"));
			ticket.setSubtitle(rs.getString("subtitle"));
			ticket.setContent(rs.getString("content"));
			ticket.setNmd(rs.getFloat("nmd"));
			ticket.setEmd(rs.getFloat("emd"));
			ticket.setHistory(rs.getString("history"));
			ticket.setAttachedfile(rs.getString("attachedfile"));
			ticket.setStarttime(rs.getTimestamp("starttime"));
			ticket.setEndtime(rs.getTimestamp("endtime"));
			return ticket;
		}
	};

	public Ticket get(Number ticketNumber) {
		return jdbcTemplate.queryForObject("select * from ticket where no = ?", new Object[] { ticketNumber }, this.userMapper);
	}

	public List<Ticket> get(final List<String> userNameList) {
		if (userNameList.size() == 0)
			return new ArrayList<Ticket>();
		
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				StringBuilder builder = new StringBuilder();
				builder.append("select * from ticket where username in (");
				for (String name : userNameList) {
					builder.append("'").append(name).append("',");
				}
				builder.replace(builder.length()-1, builder.length(), "");
				builder.append(")");
				return con.prepareStatement(builder.toString());
			}
		}, this.userMapper);
	}

	public List<Ticket> get(final String userName, final Date period) {
		final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String qeuryStr = "select * from ticket where username = '" + userName + "' AND starttime between '" + format.format(period) + "' and '" + format.format(period) + "'";
				return con.prepareStatement(qeuryStr);
			}
		}, this.userMapper);
	}
	
	public List<Ticket> getAll() {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("select * from ticket order by no");
			}
		}, this.userMapper);
	}
	
	public List<Ticket> getAllByYearAndMonth(final int year, final int month) {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {  
				String queryStr = "select * from ticket where YEAR(starttime) = " + year + " AND  MONTH(starttime) = " + month;
				return con.prepareStatement(queryStr);
			}
		}, this.userMapper);
	}

	public List<Ticket> getUserTicketByYearAndMonth(final String userName, final int year, final int month) {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {  
				String queryStr = "select * from ticket where username = '" + userName + "' AND YEAR(starttime) = " + year + " AND  MONTH(starttime) = " + month;
				return con.prepareStatement(queryStr);
			}
		}, this.userMapper);
	}
	
	public List<Ticket> getVactionTicket(Date period) {
		final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String qeuryStr = "select * from ticket where projectname = '휴가' AND starttime between '" + format.format(period) + "' and '" + format.format(period) + "'";
				return con.prepareStatement(qeuryStr);
			}
		}, this.userMapper);
	}
	
	public int getCount() {
		return jdbcTemplate.queryForObject("select count(*) from ticket", Integer.class);
	}
	
	public int delete(Number no) {
		return jdbcTemplate.update("delete from ticket where no = ?", no.longValue());
	}

}
