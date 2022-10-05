package castis.domain.ticket;

import castis.domain.dao.*;
import castis.domain.model.*;
import castis.domain.point.PointHistoryDao;
import castis.domain.project.ProjectDao;
import castis.domain.vacation.VacationHistoryDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Handles requests for the application home page.
 */
@Controller
public class TicketController {

	@Autowired
	ApplicationContext context;

	private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

	String calendarTimeFormat = "yyyy-MM-dd";
	DateFormat format = new SimpleDateFormat(calendarTimeFormat);

	@RequestMapping(value = "cal", method = RequestMethod.GET)
	public String test(Locale locale, Model model) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = user.getUsername(); // get logged in username

		PointHistoryDao pointHistoryDao = context.getBean("pointHistoryDao", PointHistoryDao.class);
		List<PointHistory> pointHistoryList = pointHistoryDao.getPointHistoryByUser(name);
		int totalPoint = 0;
		for (PointHistory pHistory : pointHistoryList) {
			if (pHistory.getUsedate() != null ) {
				//add only used code
				totalPoint += pHistory.getPoint();
			}
		}
		int userLevel = (int) Math.sqrt((int)(totalPoint/1000));
		String totalPointStr = String.format("%,d", totalPoint);
		model.addAttribute("userpoint", totalPointStr);
		model.addAttribute("username", name);
		model.addAttribute("userlevel", userLevel);
		UserDao userDao = context.getBean("userDao", UserDao.class);
		Users userInfo = userDao.get(name);

		model.addAttribute("teamname", userInfo.getTeamname());
		model.addAttribute("dailyreportlist", userInfo.getDailyreportlist());

		return "calendar";
	}

	@RequestMapping(value = "getCurrentUserInfo.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getCurrentUserInfo(Locale locale, Model model, HttpServletRequest req) throws Exception {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = user.getUsername(); // get logged in username

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("username", name);

		UserDao userDao = context.getBean("userDao", UserDao.class);
		Users userInfo = userDao.get(name);

		response.put("teamname", userInfo.getTeamname());

		return response;
	}

	public List<Event> convertTicketToEvent(List<Ticket> ticketList) {
		List<Event> events = new ArrayList<Event>();
		
		ProjectDao projectDao = context.getBean("projectDao", ProjectDao.class);
		
		List<Project> projectList = projectDao.getAll();
		
		for (Ticket ticket : ticketList) {
			Event evt = new Event();
			evt.setId(ticket.getNo().longValue());
			evt.setTitle(ticket.getTitle());
			evt.setStart(format.format(ticket.getStarttime()));
			evt.setEnd(format.format(ticket.getEndtime()));
			evt.setProject(ticket.getProjectname());
			for ( Project p : projectList ) {
				if ( p.getProjectname().equalsIgnoreCase(ticket.getProjectname())) {
					evt.setSite(p.getSite());
					evt.setBgcolor(p.getBgColor());
					break;
				}
			}
			evt.setAllDay(false);
			evt.setUsername(ticket.getUsername());
			evt.setNmd(ticket.getNmd());
			evt.setEmd(ticket.getEmd());
			evt.setContents(ticket.getContent());
			events.add(evt);
		}
		
		return events;
	}
	
	@RequestMapping(value = "getTicketDataUsingSeq.json", method = RequestMethod.GET)
	@ResponseBody
	public String getTicketDataUsingSeq(Locale locale, Model model, @RequestParam("seq") String seq, HttpServletRequest req) throws Exception {
		TicketDao ticketDao = context.getBean("ticketDao", TicketDao.class);
		List<Ticket> ticketList = new ArrayList<Ticket>();

		List<String> ids = Arrays.asList(seq.split("\\s*,\\s*"));
		for (String id : ids) {
			Number no = Long.parseLong(id);
			Ticket ticket = ticketDao.get(no);
			ticketList.add(ticket);
		}
		Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		List<Event> events = convertTicketToEvent(ticketList);

		return mGson.toJson(events);
	}

	@RequestMapping(value = "getTicketDataUsingUserNameAndPeroid.json", method = RequestMethod.POST)
	@ResponseBody
	public String getTicketDataUsingUserNameAndPeroid(Locale locale, Model model, HttpServletRequest req) throws Exception {
		TicketDao ticketDao = context.getBean("ticketDao", TicketDao.class);

		List<Ticket> ticketList = new ArrayList<Ticket>();

		if (req.getParameter("period") != null && req.getParameter("period") != "" && req.getParameter("username") != null) {
			String period = req.getParameter("period");
			String username = req.getParameter("username");
			String dateTimeFormat = "yyyy-MM-dd";
			Date peroid = new SimpleDateFormat(dateTimeFormat).parse(period);
			logger.info("get user info {} {}", username, period);
			ticketList = ticketDao.get(username, peroid);
		}

		List<Event> events = convertTicketToEvent(ticketList);

		return new Gson().toJson(events);
	}

	@RequestMapping(value = "getTicketDataUsingPeriod.json", method = RequestMethod.POST)
	@ResponseBody
	public String getTicketDataUsingPeriod(Locale locale, Model model, HttpServletRequest req) throws Exception {
		TicketDao ticketDao = context.getBean("ticketDao", TicketDao.class);
		ProjectDao projectDao = context.getBean("projectDao", ProjectDao.class);
		Holiday holidayService = context.getBean("holiday", Holiday.class);

		Map<String, String> holidayMap = holidayService.getHolidayMap();
		List<Ticket> ticketList = new ArrayList<Ticket>();
		List<Event> events = new ArrayList<Event>();
		
		if (req.getParameter("periodYear") != null && req.getParameter("periodMonth") != null) {
			String periodYear = req.getParameter("periodYear");
			String periodMonth = req.getParameter("periodMonth");
			ticketList = ticketDao.getAllByYearAndMonth(Integer.parseInt(periodYear), Integer.parseInt(periodMonth) + 1);
		}

		for (String date : holidayMap.keySet()) {
			Event evt = new Event();
			evt.setId(0L);
			evt.setTitle("");
			evt.setStart(date);
			evt.setEnd(date);
			evt.setProject(holidayMap.get(date));
			evt.setSite("");
			evt.setUsername("[휴일]");
			evt.setAllDay(false);
			evt.setBgcolor("#FF1100");
			evt.setNmd(1);
			evt.setEmd(1);
			evt.setContents("");
			events.add(evt);
		}
		
		List<Project> projectList = projectDao.getAll();
		
		for (Ticket ticket : ticketList) {
			Event evt = new Event();
			evt.setId(ticket.getNo().longValue());
			evt.setTitle(ticket.getTitle());
			evt.setStart(format.format(ticket.getStarttime()));
			evt.setEnd(format.format(ticket.getEndtime()));
			evt.setProject(ticket.getProjectname());
			for ( Project p : projectList ) {
				if ( p.getProjectname().equalsIgnoreCase(ticket.getProjectname())) {
					evt.setSite(p.getSite());
					evt.setBgcolor(p.getBgColor());
					break;
				}
			}
			evt.setUsername(ticket.getUsername());
			evt.setAllDay(false);
			evt.setUsername(ticket.getUsername());
			evt.setNmd(ticket.getNmd());
			evt.setEmd(ticket.getEmd());
			evt.setContents(ticket.getContent());
			events.add(evt);
		}
		String jsonStr = new Gson().toJson(events);
		return jsonStr;
	}

	@RequestMapping(value = "ticketdelete.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteTicketData(Locale locale, Model model, @RequestParam("seq") String seq, HttpServletRequest req) throws Exception {
		TicketDao ticketDao = context.getBean("ticketDao", TicketDao.class);
		Number no = Long.parseLong(seq);
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("resultCnt", ticketDao.delete(no));
		//delete vacation history when it is not sended
		deleteVacationHistoryByTicketNo(no.longValue());
		return response;
	}

	@RequestMapping(value = "ticketinsert.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> insertTicketData(Locale locale, Model model, @RequestParam("teamName") String teamName, @RequestParam("title") String title,
			@RequestParam("contents") String contents, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
			@RequestParam("project") String project, @RequestParam("username") String username, @RequestParam("md") String md, HttpServletRequest req)
			throws Exception {

		Float mdValue = Float.parseFloat(md);

		req.setCharacterEncoding("UTF-8");

		String dateTimeFormat = "yyyy/MM/dd";

		Date startDate = new SimpleDateFormat(dateTimeFormat).parse(startTime);
		Date endDate = new SimpleDateFormat(dateTimeFormat).parse(endTime);

		TicketDao ticketDao = context.getBean("ticketDao", TicketDao.class);
		Ticket ticket = new Ticket(teamName, username, project, title, title, contents, mdValue, mdValue, "", "", startDate, endDate);
		Number id = ticketDao.add(ticket);
		long no = id.longValue();

		Map<String, Object> response = new HashMap<String, Object>();

		response.put("resultCnt", "ok");
		response.put("no", no);
		response.put("start", format.format(ticket.getStarttime()));
		response.put("end", format.format(ticket.getEndtime()));
		PointHistoryDao pointHistoryDao = context.getBean("pointHistoryDao", PointHistoryDao.class);
		PointHistory pointHistory = new PointHistory();
		pointHistory.setCode("AUTO");
		pointHistory.setMemo("TICKET_POINT");
		pointHistory.setSender(username);
		pointHistory.setRecver(username);
		pointHistory.setPoint(5);
		pointHistory.setCreatedate(new Date());
		pointHistory.setUsedate(new Date());
		pointHistoryDao.addPointHistory(pointHistory);
		//add vacation history
		if ( project.equalsIgnoreCase("휴가") == true ) {
			addVacationHistory(no, username, contents, startDate, endDate);
		}
		return response;
	}

	private void deleteVacationHistoryByTicketNo(long ticketNumber) {
		//delete vacation history when it is not sended
		VacationHistoryDao vacationHistoryDao = context.getBean("vacationHistoryDao", VacationHistoryDao.class);
		try {
			List<VacationHistory> vhList = vacationHistoryDao.getVacationHistoryTicketNo(ticketNumber);
			for ( VacationHistory vh : vhList ) {
				if ( vh != null && vh.getStatus().equalsIgnoreCase(VacationHistory.STATUS_READY)) {
					// it is ready history. we need to delete it
					vacationHistoryDao.delete(vh.getId());
				}
			}
		} catch(Exception e) {
			logger.error("vacation delete error:{}", e);
		}
	}
	
	private void addVacationHistory(long ticketNumber, String userName, String content, Date startTime, Date endTime) {
		VacationHistoryDao vacationHistoryDao = context.getBean("vacationHistoryDao", VacationHistoryDao.class);
		do {
			VacationHistory newVactionHistory = new VacationHistory();
			newVactionHistory.setCreatedate(new Date());
			newVactionHistory.setTicketNo(ticketNumber);
			newVactionHistory.setUserId(userName);
			newVactionHistory.setSenddate(startTime);
			newVactionHistory.setStatus(VacationHistory.STATUS_READY);
			newVactionHistory.setMemo(content);
			vacationHistoryDao.addVacationHistory(newVactionHistory);
			
			Calendar c = Calendar.getInstance();
	        c.setTime(startTime);
	        c.add(Calendar.DATE, 1);
	        startTime = c.getTime();
		} while ( startTime.compareTo(endTime) <= 0);
	}
	
	@RequestMapping(value = "ticketupdate.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateTicketData(Locale locale, Model model, @RequestParam("id") String id, @RequestParam("title") String title,
			@RequestParam("contents") String contents, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
			@RequestParam("project") String project, @RequestParam("username") String username, @RequestParam("md") String md, HttpServletRequest req)
			throws Exception {

		req.setCharacterEncoding("UTF-8");

		String dateTimeFormat = "yyyy/MM/dd";

		Float mdValue = Float.parseFloat(md);
		Date startDate = new SimpleDateFormat(dateTimeFormat).parse(startTime);
		Date endDate = new SimpleDateFormat(dateTimeFormat).parse(endTime);

		TicketDao ticketDao = context.getBean("ticketDao", TicketDao.class);
		Number no = Long.parseLong(id);
		Ticket ticket = ticketDao.get(no);

		ticket.setTitle(title);
		ticket.setContent(contents);
		ticket.setStarttime(startDate);
		ticket.setEndtime(endDate);
		ticket.setProjectname(project);
		ticket.setUsername(username);
		ticket.setNmd(mdValue);
		ticket.setEmd(mdValue);

		Number isSuccess = ticketDao.update(ticket);

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("resultCnt", "ok");
		response.put("isSuccess", isSuccess);

		//update vacation history
		if ( project.equalsIgnoreCase("휴가") == true ) {
			deleteVacationHistoryByTicketNo(no.longValue());
			addVacationHistory(no.longValue(), username, contents, startDate, endDate);
		}
		return response;
	}

	@RequestMapping(value = "ticketupdatetime.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateTicketTimeData(Locale locale, Model model, @RequestParam("id") String id, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime, HttpServletRequest req) throws Exception {

		req.setCharacterEncoding("UTF-8");

		Date startDate = new SimpleDateFormat(calendarTimeFormat).parse(startTime);
		Date endDate = new SimpleDateFormat(calendarTimeFormat).parse(endTime);

		TicketDao ticketDao = context.getBean("ticketDao", TicketDao.class);
		Number no = Long.parseLong(id);
		Ticket ticket = ticketDao.get(no);
		ticket.setStarttime(startDate);
		ticket.setEndtime(endDate);

		Number isSuccess = ticketDao.update(ticket);

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("resultCnt", "ok");
		response.put("isSuccess", isSuccess);
		//update vacation history
		if ( ticket.getProjectname().equalsIgnoreCase("휴가") == true ) {
			deleteVacationHistoryByTicketNo(no.longValue());
			addVacationHistory(no.longValue(), ticket.getUsername(), ticket.getContent(), startDate, endDate);
		}
		return response;
	}

	@RequestMapping(value = "sendDailyReportEmail.json", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> sendDailyReportEmail(HttpServletRequest req) throws Exception {
		logger.info("send mail sendDailyReportEmail");

		String sendUserName = req.getParameter("username");
		String title = "[업무보고] " + req.getParameter("title");
		Map<String, Object> response = new HashMap<String, Object>();

		UserDao userDao = context.getBean("userDao", UserDao.class);
		Users user = userDao.get(sendUserName);
		if (user == null) {
			logger.error("get user info fail");
			response.put("isSuccess", false);
			return response;
		}

		StringBuilder builder = new StringBuilder();
		builder.append("<font style=\"font-family: 맑은 고딕; font-size:10pt\">안녕하세요.<br>");
		builder.append(user.getTeamname()).append("팀 ").append(sendUserName).append(" ").append(req.getParameter("title")).append("<br><br>");
		builder.append(req.getParameter("contents").replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;"));
		builder.append("<br><br>이 메일은 IMS(Issue Management System)에서 자동으로 발송한 메일입니다.(http://teatime.castis.net/)</font>");

		boolean sessionDebug = false;
		Properties props = System.getProperties();
		props.put("mail.host", "mail.castis.com");
		props.put("mail.smtp.port", "25");
		props.put("mail.transport.protocol", "smtp");
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(sessionDebug);
		try {
			// Multipart
			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();
			// Now set the actual message
			messageBodyPart.setContent(builder.toString(), "text/html; charset=utf-8");
			multipart.addBodyPart(messageBodyPart);
			if (req.getParameter("filename").isEmpty() == false) {
				String[] filelist = req.getParameterValues("filename");
				for (String name : filelist) {
					messageBodyPart = new MimeBodyPart();
					String filename = req.getSession().getServletContext().getRealPath("/") + "upload/" + name;
					DataSource source = new FileDataSource(filename);
					messageBodyPart.setDataHandler(new DataHandler(source));
					messageBodyPart.setFileName(MimeUtility.encodeText(name, "UTF-8", null));
					// messageBodyPart.setHeader("Content-type",
					// "text/plain; charset=utf-8");
					multipart.addBodyPart(messageBodyPart);
				}
			}

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(user.getEmail()));
			InternetAddress[] address = InternetAddress.parse(user.getDailyreportlist(), false);
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setRecipient(Message.RecipientType.CC, new InternetAddress(user.getEmail()));
			msg.setSubject(title);
			msg.setSentDate(new Date());
			msg.setContent(multipart);

			Transport.send(msg);
			// file remove
			String[] filelist = req.getParameterValues("filename");
			for (String name : filelist) {
				String filename = req.getSession().getServletContext().getRealPath("/") + "upload/" + name;
				File remainFile = new File(filename);
				if (remainFile.delete() == true) {
					logger.info("file {} is deleted", name);
				}
			}
			response.put("isSuccess", true);
			logger.info("send mail {} to {} success", sendUserName, user.getEmail());
		} catch (MessagingException mex) {
			logger.error("{}", mex.getMessage());
			response.put("isSuccess", false);
			response.put("errorString", mex.getMessage());
			mex.printStackTrace();
		}

		return response;
	}

	@RequestMapping(value = "fileClear", method = RequestMethod.POST)
	@ResponseBody
	public void fileClear(HttpServletRequest req) {
		// 저장 경로 설정
		String[] filelist = req.getParameterValues("filename");
		for (String name : filelist) {
			String filename = req.getSession().getServletContext().getRealPath("/") + "upload/" + name;
			File remainFile = new File(filename);
			if (remainFile.delete() == true) {
				logger.info("file {} is deleted", name);
			}
		}
	}

	@RequestMapping(value = "fileUpload", method = RequestMethod.POST)
	@ResponseBody
	public void fileUpload(HttpServletRequest request) {
		// 저장 경로 설정
		String root = request.getSession().getServletContext().getRealPath("/");
		String path = root + "upload/";

		String newFileName = ""; // 업로드 되는 파일명
		MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
		logger.info("this is 'file upload' page.");

		File dir = new File(path);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}

		Iterator<String> files = req.getFileNames();
		while (files.hasNext()) {
			String uploadFile = files.next();

			MultipartFile mFile = req.getFile(uploadFile);
			String fileName = mFile.getOriginalFilename();
			if (fileName == "")
				continue;
			logger.info("파일 이름 : " + fileName);
			newFileName = fileName;
			try {
				mFile.transferTo(new File(path + newFileName));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "projectData.json")
	public @ResponseBody String getProjectData(HttpServletResponse response) {

		ProjectDao projectDao = context.getBean("projectDao", ProjectDao.class);
		List<Project> projectList = projectDao.getAll();
		List<Project> projects = new ArrayList<Project>();
		Date today = new Date();
		for (Project project : projectList) {
			if ( today.compareTo(project.getEnddate()) < 0 ) {
				Project prj = new Project();
				prj.setProjectname(project.getProjectname());
				prj.setSite(project.getSite());
				projects.add(prj);
			}
		}
		Collections.sort(projects);
		String jsonStr = new Gson().toJson(projects);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		return jsonStr;
	}

	@RequestMapping(value = "userData.json")
	public @ResponseBody String getUserData(HttpServletResponse response) {	

		UserDao userDao = context.getBean("userDao", UserDao.class);
		List<Users> userList = userDao.getAll();
		List<Users> users = new ArrayList<Users>();

		for (Users user : userList) {
			Users usr = new Users();
			usr.setUsername(user.getUsername());
			usr.setTeamname(user.getTeamname());
			users.add(usr);
		}

		String jsonStr = new Gson().toJson(users);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		return jsonStr;
	}

	@RequestMapping(value = "monthlystat/{currentYear}/{currentMonth}", method = RequestMethod.GET)
	public String MonthlyStat(@PathVariable("currentYear") int currentYear, @PathVariable("currentMonth") int currentMonth, Model model) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = user.getUsername(); // get logged in username
		PointHistoryDao pointHistoryDao = context.getBean("pointHistoryDao", PointHistoryDao.class);
		List<PointHistory> pointHistoryList = pointHistoryDao.getPointHistoryByUser(name);
		int totalPoint = 0;
		for (PointHistory pHistory : pointHistoryList) {
			if (pHistory.getUsedate() != null ) {
				//add only used code
				totalPoint += pHistory.getPoint();
			}
		}
		int userLevel = (int) Math.sqrt((int)(totalPoint/1000));
		String totalPointStr = String.format("%,d", totalPoint);
		model.addAttribute("userpoint", totalPointStr);
		model.addAttribute("username", name);
		model.addAttribute("userlevel", userLevel);

		logger.info("this is 'monthlystat" + "/" + currentYear + "/" + currentMonth + "'");
		TicketDao ticketDao = context.getBean("ticketDao", TicketDao.class);
		ProjectDao projectDao = context.getBean("projectDao", ProjectDao.class);

		// [key]username, [value]userStatOnMonth object list
		Map<String, List<UserStatOnMonth>> userStatMap = new HashMap<String, List<UserStatOnMonth>>();
		List<Ticket> ticketListByMonth = ticketDao.getUserTicketByYearAndMonth(name, currentYear, currentMonth);
		for (Ticket t : ticketListByMonth) {
			if (t.getUsername().equalsIgnoreCase("guest")) {
				continue;
			}
			if (userStatMap.containsKey(t.getUsername()) == true) {
				boolean isFindfromList = false;
				// already in map for user
				List<UserStatOnMonth> madeList = userStatMap.get(t.getUsername());
				for (UserStatOnMonth u : madeList) {
					if (u.getProject().equalsIgnoreCase(t.getProjectname()) == true) {
						// there is same project (only sum to nmd/emd)
						isFindfromList = true;
						u.setNmd(u.getNmd() + t.getNmd());
						u.setEmd(u.getEmd() + t.getEmd());
					}
				}
				if (isFindfromList == false) {
					// this is new project for this user
					String site = projectDao.get(t.getProjectname()).getSite();
					UserStatOnMonth stat = new UserStatOnMonth(t.getUsername(), site, t.getProjectname(), t.getNmd(), t.getEmd());
					madeList.add(stat);
				}
			} else {
				// insert first time
				String site = projectDao.get(t.getProjectname()).getSite();
				UserStatOnMonth stat = new UserStatOnMonth(t.getUsername(), site, t.getProjectname(), t.getNmd(), t.getEmd());
				List<UserStatOnMonth> newUserStatList = new ArrayList<UserStatOnMonth>();
				newUserStatList.add(stat);
				userStatMap.put(t.getUsername(), newUserStatList);
			}
		}
		model.addAttribute("year", currentYear);
		model.addAttribute("month", currentMonth);
		model.addAttribute("userStatMap", userStatMap);
		return "monthlystat";
	}

	@RequestMapping(value = "dailyreport", method = RequestMethod.GET)
	public String daily_report(Locale locale, Model model) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = user.getUsername(); // get logged in username
		PointHistoryDao pointHistoryDao = context.getBean("pointHistoryDao", PointHistoryDao.class);
		List<PointHistory> pointHistoryList = pointHistoryDao.getPointHistoryByUser(name);
		int totalPoint = 0;
		for (PointHistory pHistory : pointHistoryList) {
			if (pHistory.getUsedate() != null ) {
				//add only used code
				totalPoint += pHistory.getPoint();
			}
		}
		int userLevel = (int) Math.sqrt((int)(totalPoint/1000));
		String totalPointStr = String.format("%,d", totalPoint);
		model.addAttribute("userpoint", totalPointStr);
		model.addAttribute("username", name);
		model.addAttribute("userlevel", userLevel);
		UserDao userDao = context.getBean("userDao", UserDao.class);
		Users userInfo = userDao.get(name);
		model.addAttribute("dailyreportlist", userInfo.getDailyreportlist());

		return "dailyreport";
	}

	@RequestMapping(value = "jobstats/{currentYear}", method = RequestMethod.GET)
	public String JobStats(@PathVariable("currentYear") int currentYear, Model model) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = user.getUsername(); // get logged in username

		logger.info("this is 'jobstats" + "/" + currentYear + "'");
		TicketDao ticketDao = context.getBean("ticketDao", TicketDao.class);

		// [key]username, [value]userStatOnMonth object list
		Map<Integer, List<Ticket>> jobStatMap = new HashMap<Integer, List<Ticket>>();
		for (int currentMonth = 1; currentMonth < 13; currentMonth++) {
			List<Ticket> ticketListByMonth = ticketDao.getUserTicketByYearAndMonth(name, currentYear, currentMonth);
			for (Ticket t : ticketListByMonth) {
				t.setContent("");
			}
			jobStatMap.put(currentMonth, ticketListByMonth);
		}
		PointHistoryDao pointHistoryDao = context.getBean("pointHistoryDao", PointHistoryDao.class);
		List<PointHistory> pointHistoryList = pointHistoryDao.getPointHistoryByUser(name);
		int totalPoint = 0;
		for (PointHistory pHistory : pointHistoryList) {
			if (pHistory.getUsedate() != null ) {
				//add only used code
				totalPoint += pHistory.getPoint();
			}
		}
		int userLevel = (int) Math.sqrt((int)(totalPoint/1000));
		String totalPointStr = String.format("%,d", totalPoint);
		model.addAttribute("userpoint", totalPointStr);
		model.addAttribute("username", name);
		model.addAttribute("userlevel", userLevel);
		model.addAttribute("year", currentYear);
		model.addAttribute("month", 0);
		model.addAttribute("jobstatsmap", jobStatMap);
		return "jobstats";
	}
	
	@RequestMapping(value = "jobstats/{currentYear}/{currentMonth}", method = RequestMethod.GET)
	public String JobStatsAtMonth(@PathVariable("currentYear") int currentYear, @PathVariable("currentMonth") int currentMonth, Model model) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = user.getUsername(); // get logged in username

		logger.info("this is 'jobstats" + "/" + currentYear + "/" + currentMonth + "'");
		TicketDao ticketDao = context.getBean("ticketDao", TicketDao.class);

		// [key]username, [value]userStatOnMonth object list
		Map<Integer, List<Ticket>> jobStatMap = new HashMap<Integer, List<Ticket>>();
		List<Ticket> ticketListByMonth = ticketDao.getUserTicketByYearAndMonth(name, currentYear, currentMonth);
		for (Ticket t : ticketListByMonth) {
			t.setContent("");
		}
		jobStatMap.put(currentMonth, ticketListByMonth);
		PointHistoryDao pointHistoryDao = context.getBean("pointHistoryDao", PointHistoryDao.class);
		List<PointHistory> pointHistoryList = pointHistoryDao.getPointHistoryByUser(name);
		int totalPoint = 0;
		for (PointHistory pHistory : pointHistoryList) {
			if (pHistory.getUsedate() != null ) {
				//add only used code
				totalPoint += pHistory.getPoint();
			}
		}
		int userLevel = (int) Math.sqrt((int)(totalPoint/1000));
		String totalPointStr = String.format("%,d", totalPoint);
		model.addAttribute("userpoint", totalPointStr);
		model.addAttribute("username", name);
		model.addAttribute("userlevel", userLevel);
		model.addAttribute("year", currentYear);
		model.addAttribute("month", currentMonth);
		model.addAttribute("jobstatsmap", jobStatMap);
		return "jobstats";
	}
}
