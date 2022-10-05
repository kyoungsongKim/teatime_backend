package castis.domain.vacation;

import castis.domain.model.Vacation;

import java.util.List;

public interface VacationDao {
	public void add(Vacation vacation);

	public void deleteAll();

	public Vacation get(String username);

	public List<Vacation> getAll();

	public int getCount();
}
