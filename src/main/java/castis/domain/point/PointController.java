package castis.domain.point;

import castis.domain.dao.UserDao;
import castis.domain.model.PointHistory;
import castis.domain.model.Users;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequiredArgsConstructor
public class PointController {

    private UserDao userDao;
    private PointHistoryDao pointHistoryDao;
    private static final Logger logger = LoggerFactory.getLogger(PointController.class);

    String calendarTimeFormat = "yyyy-MM-dd";
    DateFormat format = new SimpleDateFormat(calendarTimeFormat);

    @RequestMapping(value = "pointhistory/{currentYear}", method = RequestMethod.GET)
    public String getPointHistory(@PathVariable("currentYear") int currentYear, Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user.getUsername(); // get logged in username

        List<Users> userInfoList = userDao.getAll();
        List<String> userIdList = new ArrayList<String>();
        Map<String, AtomicInteger> userPointMap = new HashMap<>();
        for (Users curUser : userInfoList) {
            userPointMap.put(curUser.getUserid(), new AtomicInteger(0));
            if (curUser.getUserid().equalsIgnoreCase(name) == false) {
                userIdList.add(curUser.getUserid());
            }
        }
        logger.info("this is 'pointhistory" + "/" + currentYear + "'");
        List<PointHistory> pointHistoryList = pointHistoryDao.getAllPointHistory();
        int totalPoint = 0;
        List<PointHistory> pointHistoryThisYearList = new ArrayList<PointHistory>();
        for (PointHistory pHistory : pointHistoryList) {
            if (pHistory.getUsedate() != null) {
                if (userPointMap.containsKey(pHistory.getRecver())) {
                    AtomicInteger currentPoint = userPointMap.get(pHistory.getRecver());
                    currentPoint.addAndGet(pHistory.getPoint());
                }
                if (pHistory.getRecver().equalsIgnoreCase(name)) {
                    //only my
                    totalPoint += pHistory.getPoint();
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(pHistory.getUsedate());
                    int year = calendar.get(Calendar.YEAR);
                    if (year == currentYear) {
                        pointHistoryThisYearList.add(pHistory);
                    }
                }
            }
        }
        List<String> userPointList = new ArrayList<String>();
        Iterator<String> keys = userPointMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String userPoint = key + ":" + userPointMap.get(key).toString();
            userPointList.add(userPoint);
            System.out.println(userPoint);
        }

        int userLevel = (int) Math.sqrt((int) (totalPoint / 1000));
        String totalPointStr = String.format("%,d", totalPoint);
        model.addAttribute("userpoint", totalPointStr);
        model.addAttribute("username", name);
        model.addAttribute("userlevel", userLevel);
        model.addAttribute("year", currentYear);
        model.addAttribute("pointHistoryList", pointHistoryThisYearList);
        model.addAttribute("userIdList", userIdList);
        model.addAttribute("userPointList", userPointList);
        return "my_point";
    }

    @RequestMapping(value = "pointhistory", method = RequestMethod.GET)
    public String getTotalPointHistory(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user.getUsername(); // get logged in username

        List<Users> userInfoList = userDao.getAll();
        List<String> userIdList = new ArrayList<String>();
        Map<String, AtomicInteger> userPointMap = new HashMap<>();
        for (Users curUser : userInfoList) {
            userPointMap.put(curUser.getUserid(), new AtomicInteger(0));
            if (curUser.getUserid().equalsIgnoreCase(name) == false) {
                userIdList.add(curUser.getUserid());
            }
        }
        logger.info("this is 'getTotalPointHistory");
        List<PointHistory> pointHistoryList = pointHistoryDao.getAllPointHistory();
        int totalPoint = 0;
        List<PointHistory> pointHistoryThisYearList = new ArrayList<PointHistory>();
        for (PointHistory pHistory : pointHistoryList) {
            if (pHistory.getUsedate() != null) {
                if (userPointMap.containsKey(pHistory.getRecver())) {
                    AtomicInteger currentPoint = userPointMap.get(pHistory.getRecver());
                    currentPoint.addAndGet(pHistory.getPoint());
                }
                if (pHistory.getRecver().equalsIgnoreCase(name)) {
                    //only my
                    totalPoint += pHistory.getPoint();
                    pointHistoryThisYearList.add(pHistory);
                }
            }
        }
        List<String> userPointList = new ArrayList<String>();
        Iterator<String> keys = userPointMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String userPoint = key + ":" + userPointMap.get(key).toString();
            userPointList.add(userPoint);
            System.out.println(userPoint);
        }

        int userLevel = (int) Math.sqrt((int) (totalPoint / 1000));
        String totalPointStr = String.format("%,d", totalPoint);
        model.addAttribute("year", 0);
        model.addAttribute("userpoint", totalPointStr);
        model.addAttribute("username", name);
        model.addAttribute("userlevel", userLevel);
        model.addAttribute("pointHistoryList", pointHistoryThisYearList);
        model.addAttribute("userIdList", userIdList);
        model.addAttribute("userPointList", userPointList);
        return "my_point";
    }

    @RequestMapping(value = "pointcode.do", method = RequestMethod.POST)
    @ResponseBody
    public String getPointCode(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam("memo") String memo, @RequestParam("userId") String userId, @RequestParam("point") String pointStr) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String name = user.getUsername(); // get logged in username

            request.setCharacterEncoding("UTF-8");
            logger.info("getPointCode userId:{} pointStr:{} memo:{}", userId, pointStr, memo);
            int point = Integer.parseInt(pointStr);
            PointHistory newPointHistory = new PointHistory();
            newPointHistory.setSender(name);
            newPointHistory.setRecver(userId);
            newPointHistory.setMemo(memo);
            newPointHistory.setCreatedate(new Date());
            newPointHistory.setPoint(point);
            UUID uuid = UUID.randomUUID();
            long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
            String shortUUID = Long.toString(l, Character.MAX_RADIX);
            shortUUID = shortUUID.toUpperCase().substring(0, 4);
            newPointHistory.setCode(shortUUID);
            pointHistoryDao.addPointHistory(newPointHistory);
            logger.info("getPointCode shortUUID:{}", shortUUID);
            return shortUUID;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(500, "internal server error");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return "fail";
    }

    @RequestMapping(value = "inputcode.do", method = RequestMethod.POST)
    @ResponseBody
    public String processInputCoce(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam("inputPointCode") String inputPointCode) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String name = user.getUsername(); // get logged in username
            inputPointCode = inputPointCode.toUpperCase();
            logger.info("getPoiprocessInputCoce inputPointCode:{}", inputPointCode);
            PointHistory pointHistory = pointHistoryDao.getByCode(inputPointCode);
            if (pointHistory == null) {
                return "fail-[" + inputPointCode + "] is not exist.";
            }
            if (pointHistory.getRecver().equalsIgnoreCase(name) == false) {
                return "fail-this CODE can \r\nuse only [" + pointHistory.getRecver() + "]";
            }
            pointHistory.setCode(pointHistory.getCode() + "_COMPLETE");
            pointHistory.setUsedate(new Date());
            pointHistoryDao.update(pointHistory);
            return String.valueOf(pointHistory.getPoint());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(500, "internal server error");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return "fail";
    }
}
