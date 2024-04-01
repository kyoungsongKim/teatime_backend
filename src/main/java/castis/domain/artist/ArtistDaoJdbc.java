package castis.domain.artist;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ArtistDaoJdbc implements ArtistDao {
    private JdbcTemplate jdbcTemplate;

    public ArtistDaoJdbc() {
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void add(Artist artist) {
        if (artist != null) {
            this.jdbcTemplate.update(
                    "insert into artist(userid,username,realname,teamname,thumbnail,onelinetitle,skills,projectlist) values(?,?,?,?,?,?,?,?)",
                    artist.getUserid(), artist.getUsername(), artist.getRealname(), artist.getTeamname(),
                    artist.getThumbnail(), artist.getOnelinetitle(), artist.getSkills(), artist.getProjectlist());
        }
    }

    private RowMapper<Artist> userMapper = new RowMapper<Artist>() {
        @Override
        public Artist mapRow(ResultSet rs, int rowNum) throws SQLException {
            Artist artist = new Artist();
            artist.setId(rs.getLong("id"));
            artist.setUserid(rs.getString("userid"));
            artist.setUsername(rs.getString("username"));
            artist.setRealname(rs.getString("realname"));
            artist.setTeamname(rs.getString("teamname"));
            artist.setThumbnail(rs.getString("thumbnail"));
            artist.setOnelinetitle(rs.getString("onelinetitle"));
            artist.setSkills(rs.getString("skills"));
            artist.setProjectlist(rs.getString("projectlist"));
            return artist;
        }
    };

    @Override
    public List<Artist> getAllArtist() {
        return jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                return con.prepareStatement("select * from artist");
            }
        }, this.userMapper);
    }

    public int getCount() {
        return jdbcTemplate.queryForObject("select count(*) from artist", Integer.class);
    }

    @Override
    public Artist get(long id) {
        return jdbcTemplate.queryForObject("select * from artist where id = ?", new Object[] { id }, this.userMapper);
    }

}
