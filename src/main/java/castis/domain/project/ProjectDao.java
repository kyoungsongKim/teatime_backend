package castis.domain.project;

import castis.domain.model.Project;

import java.util.Date;
import java.util.List;

public interface ProjectDao {
	public void add(Project project);
	
	public Number updateEndDate(final Date endDate, final String site, final String projectname);

	public int deleteSiteAndProjectName(String site, String projectName);
	
	public void deleteAll();

	public Project get(String projectname);
	
	public Project getBySite(String site);

	public List<Project> getAll();

	public int getCount();
}
