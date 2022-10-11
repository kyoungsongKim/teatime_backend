package castis.domain.user;

import castis.domain.model.Users;

import java.util.List;

public interface UserDao {
	public void add(Users user);

	public void deleteAll();

	public Users get(String username);

	public List<Users> getAll();

	public int getCount();
	
	public Number update(Users user);
}
