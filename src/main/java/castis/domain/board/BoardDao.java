package castis.domain.board;

import castis.domain.model.Users;

import java.util.List;

public interface BoardDao {
	public void add(Users user);

	public void deleteAll();

	public Users get(String username);

	public List<Users> getAll();

	public int getCount();
}
