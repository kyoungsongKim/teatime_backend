package castis.domain.authorities;

import castis.domain.model.Authorities;

import java.util.List;

public interface AuthoritiesDao {
	public void add(Authorities authorities);

	public void deleteAll();

	public Authorities get(String username);

	public List<Authorities> getAll();

	public int getCount();
}
