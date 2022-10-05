package castis.domain.point;

import castis.domain.model.PointHistory;

import java.util.List;

public interface PointHistoryDao {
	public Number addPointHistory(PointHistory pointHistory);

	public Number update(PointHistory pointHistory);
	
	public PointHistory get(Number pointHistoryNumber);
	
	public PointHistory getByCode(String code);
	
	public List<PointHistory> getAllPointHistory();
	
	public List<PointHistory> getPointHistoryByUser(String recver);
	
	public List<PointHistory> getPointHistoryByYear(String recver,int year);
	
	public List<PointHistory> getPointHistoryByYearAndMonth(int year, int month);
}
