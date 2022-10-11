package castis.web;

import castis.domain.authorities.AuthoritiesDao;
import castis.domain.model.*;
import castis.domain.point.PointHistoryDao;
import castis.domain.project.ProjectDao;
import castis.domain.team.TeamDao;
import castis.domain.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Handles requests for the application home page.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private BCryptPasswordEncoder passwordEncoder;

    private TeamDao teamDao;
    private UserDao userDao;
    private PointHistoryDao pointHistoryDao;
    private AuthoritiesDao authoritiesDao;
    private ProjectDao projectDao;

    /**
     * Simply selects the home view to render by returning its name.
     */
    @RequestMapping(value = {"/login_page", "/"}, method = RequestMethod.GET)
    public String home(Locale locale, Model model) {
        log.info("this is 'login' page. {}.", locale);
        return "login_page";
    }

    @RequestMapping(value = "/register_member", method = RequestMethod.GET)
    public ModelAndView showRegistrationForm(@ModelAttribute Users user) {
        log.info("Here is /register_member");
        List<Team> teamList = teamDao.getAll();
        List<String> teamNameList = new ArrayList<String>();
        for (Team t : teamList) {
            teamNameList.add(t.getTeamname());
        }
        teamNameList.add("직접입력");
        Map<String, List<String>> teammap = new HashMap<String, List<String>>();
        teammap.put("teamlist", teamNameList);
        return new ModelAndView("register_member", "teammap", teammap);
    }

    @RequestMapping(value = "/join_member", method = RequestMethod.POST)
    public String joinMember(@ModelAttribute Users user, HttpServletRequest req) throws UnsupportedEncodingException {
        req.setCharacterEncoding("UTF-8");

        List<Team> teamList = teamDao.getAll();
        List<String> teamNameList = new ArrayList<String>();
        for (Team t : teamList) {
            teamNameList.add(t.getTeamname());
        }
        if (teamNameList.indexOf(user.getTeamname()) == -1) {
            Team team = new Team(user.getTeamname(), "temp");
            teamDao.add(team);
        }
        user.setEnabled(true);
        user.setUserid(user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.add(user);
        Authorities newAuthority = new Authorities(user.getUsername(), "ROLE_USER");
        authoritiesDao.add(newAuthority);
        return "login_page";
    }

    @RequestMapping(value = "/my_info", method = RequestMethod.GET)
    public String myInfo(Locale locale, Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user.getUsername(); // get logged in username
        List<PointHistory> pointHistoryList = pointHistoryDao.getPointHistoryByUser(name);
        int totalPoint = 0;
        for (PointHistory pHistory : pointHistoryList) {
            if (pHistory.getUsedate() != null) {
                //add only used code
                totalPoint += pHistory.getPoint();
            }
        }
        Users curUser = userDao.get(user.getUsername());
        int userLevel = (int) Math.sqrt((int) (totalPoint / 1000));
        String totalPointStr = String.format("%,d", totalPoint);
        model.addAttribute("userpoint", totalPointStr);
        model.addAttribute("username", name);
        model.addAttribute("useremail", curUser.getEmail());
        model.addAttribute("userlevel", userLevel);
        model.addAttribute("dailyreportlist", curUser.getDailyreportlist());
        model.addAttribute("vacationreportlist", curUser.getVacationReportList());
        return "my_info";
    }

    @RequestMapping(value = "/change_myinfo", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> changeMyInfo(HttpServletRequest req, @RequestParam("password") String password,
                                            @RequestParam("dailyreportlist") String dailyreportlist,
                                            @RequestParam("vacationreportlist") String vacationreportlist) throws UnsupportedEncodingException {
        req.setCharacterEncoding("UTF-8");
        User loginuser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = loginuser.getUsername(); // get logged in username
        Users curUser = userDao.get(name);
        curUser.setEnabled(true);
        if (password != null && password.isEmpty() == false) {
            curUser.setPassword(passwordEncoder.encode(password));
        }
        curUser.setDailyreportlist(dailyreportlist);
        curUser.setVacationReportList(vacationreportlist);
        userDao.update(curUser);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("resultCnt", "ok");
        return response;
    }

    @RequestMapping(value = "/service_list", method = RequestMethod.GET)
    public String serviceList(Locale locale, Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user.getUsername(); // get logged in username
        List<PointHistory> pointHistoryList = pointHistoryDao.getPointHistoryByUser(name);
        int totalPoint = 0;
        for (PointHistory pHistory : pointHistoryList) {
            if (pHistory.getUsedate() != null) {
                //add only used code
                totalPoint += pHistory.getPoint();
            }
        }
        int userLevel = (int) Math.sqrt((int) (totalPoint / 1000));
        String totalPointStr = String.format("%,d", totalPoint);
        model.addAttribute("userpoint", totalPointStr);
        model.addAttribute("userlevel", userLevel);
        model.addAttribute("username", name);
        return "service_list";
    }

    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    public String configuration(Locale locale, Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Project> projectList = projectDao.getAll();
        List<String> siteList = new ArrayList<String>();
        Map<String, List<Project>> projectMap = new HashMap<String, List<Project>>();
        int siteCount = 0;
        int maxProjectCount = 0;
        Date today = new Date();
        for (Project p : projectList) {
            if (today.compareTo(p.getEnddate()) < 0) {
                // only valid project ( current time < endtime )
                if (projectMap.containsKey(p.getSite()) == false) {
                    List<Project> projectNewList = new ArrayList<Project>();
                    projectNewList.add(p);
                    siteList.add(p.getSite());
                    // new site
                    projectMap.put(p.getSite(), projectNewList);
                    siteCount++;
                } else {
                    List<Project> createdProjectList = projectMap.get(p.getSite());
                    createdProjectList.add(p);
                    if (createdProjectList.size() > maxProjectCount) {
                        maxProjectCount = createdProjectList.size();
                    }
                }
            }
        }
        String name = user.getUsername(); // get logged in username
        List<PointHistory> pointHistoryList = pointHistoryDao.getPointHistoryByUser(name);
        int totalPoint = 0;
        for (PointHistory pHistory : pointHistoryList) {
            if (pHistory.getUsedate() != null) {
                //add only used code
                totalPoint += pHistory.getPoint();
            }
        }
        int userLevel = (int) Math.sqrt((int) (totalPoint / 1000));
        String totalPointStr = String.format("%,d", totalPoint);
        model.addAttribute("userpoint", totalPointStr);
        model.addAttribute("username", name);
        model.addAttribute("userlevel", userLevel);
        model.addAttribute("sitelist", siteList);
        model.addAttribute("projectmap", projectMap);
        model.addAttribute("sitecount", siteCount);
        model.addAttribute("maxprojectcount", maxProjectCount);
        return "configuration";
    }

    @RequestMapping(value = "/add_project", method = RequestMethod.POST)
    public String addProject(@ModelAttribute Project project, HttpServletRequest req) throws UnsupportedEncodingException {
        req.setCharacterEncoding("UTF-8");

        String site = project.getSite();
        String projectName = project.getProjectname();
        String porjectDescription = projectName;
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.YEAR, 1);
        Date oneYearAfter = cal.getTime();
        Project sameSiteProject = projectDao.getBySite(site);
        projectDao.add(new Project(projectName, site, porjectDescription, now, oneYearAfter, sameSiteProject.getBgColor()));
        return "redirect:/configuration";
    }

    @RequestMapping(value = "updateEndDate", method = RequestMethod.GET)
    @ResponseBody
    public String updateEndDate(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam("site") String site, @RequestParam("projectname") String projectname) {
        try {
            request.setCharacterEncoding("UTF-8");
            log.info("deleteProject site:{} projectname:{}", site, projectname);
            Number isSuccess = projectDao.updateEndDate(new Date(), site, projectname);
            log.info("deleteProject isSuccess:{}", isSuccess);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            try {
                response.sendError(500, "internal server error");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return "fail";
        }
        return "ok";
    }
}
