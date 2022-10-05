package castis.domain.artist;


import java.util.List;

public interface ArtistDao {
	public void add(Artist artist);

	public Artist get(long id);

	public List<Artist> getAllArtist();

	public int getCount();
}
