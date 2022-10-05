package castis.domain.vacation;

import castis.domain.model.VacationHistory;

import java.util.Date;
import java.util.List;

public interface VacationHistoryDao {
	public Number addVacationHistory(VacationHistory vacationHistory);

	public Number update(VacationHistory vacationHistory);
	
	public List<VacationHistory> getAllVacationHistory();
	
	public List<VacationHistory> getVacationHistoryByUser(String userId);
	
	public List<VacationHistory> getVacationHistoryBySendDate(Date sendDate);
	
	public List<VacationHistory> getVacationHistoryTicketNo(Long ticketNo);
	
	public VacationHistory get(Number id);
	
	public List<VacationHistory> getVacationHistoryByYear(String recver,int year);
	
	public int delete(Number no);
}
