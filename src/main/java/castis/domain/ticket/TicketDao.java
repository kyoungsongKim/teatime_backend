package castis.domain.ticket;

import castis.domain.model.Ticket;

import java.util.Date;
import java.util.List;

public interface TicketDao {
	public Number add(Ticket ticket);

	public Number update(Ticket ticket);
	
	public Number updateTime(Ticket ticket);
	
	public void deleteAll();

	public Ticket get(Number ticketNumber);
	
	public List<Ticket> get(List<String> userNameList);
	
	public List<Ticket> get(String userName, Date period);

	public List<Ticket> getAll();
	
	public List<Ticket> getAllByYearAndMonth(int year, int month);
	
	public List<Ticket> getUserTicketByYearAndMonth(String userName, int year, int month);
	
	public List<Ticket> getVactionTicket(Date period);

	public int getCount();
	
	public int delete(Number no);
}
