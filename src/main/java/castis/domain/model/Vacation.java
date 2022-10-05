package castis.domain.model;

import java.util.Date;

public class Vacation {
	private String username;
	private int vacationyear;
	private int vacationreward;
	private int vacationspeacial;
	private Date resetdate;

	public Vacation() {

	}

	public Vacation(String username, int vacationyear, int vacationreward, int vacationspeacial, Date resetdate) {
		this.username = username;
		this.vacationyear = vacationyear;
		this.vacationreward = vacationreward;
		this.vacationspeacial = vacationspeacial;
		this.resetdate = resetdate;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getVacationyear() {
		return vacationyear;
	}

	public void setVacationyear(int vacationyear) {
		this.vacationyear = vacationyear;
	}

	public int getVacationreward() {
		return vacationreward;
	}

	public void setVacationreward(int vacationreward) {
		this.vacationreward = vacationreward;
	}

	public int getVacationspeacial() {
		return vacationspeacial;
	}

	public void setVacationspeacial(int vacationspeacial) {
		this.vacationspeacial = vacationspeacial;
	}

	public Date getResetdate() {
		return resetdate;
	}

	public void setResetdate(Date resetdate) {
		this.resetdate = resetdate;
	}
}
